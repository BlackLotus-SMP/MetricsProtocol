#!/usr/bin/python3

import json
import socket
import struct


def write_ushort(n: int) -> bytes:
    return struct.pack('>H', n)


def write_string(s: str) -> bytes:
    return struct.pack('>b', len(s)) + s.encode('utf-8')


def r_varint(s: socket.socket) -> int:
    data: int = 0
    for i in range(5):
        ordinal: bytes = s.recv(1)
        if len(ordinal) == 0:
            break
        byte = ord(ordinal)
        data |= (byte & 0x7F) << 7 * i
        if not byte & 0x80:
            break
    return data


def w_varint(n: int) -> bytes:
    varint: bytes = b''
    while True:
        byte: int = n & 0x7F
        n >>= 7
        varint += struct.pack('B', byte | (0x80 if n > 0 else 0))
        if n == 0:
            break
    return varint


def main():
    ip: socket = "127.0.0.1"
    port: int = 25565

    sock: socket.socket = socket.socket()
    sock.connect((ip, port))
    try:
        data: bytes = b'\x00'
        data += b'\xff\xff\xff\xff\x0f'
        data += write_string(ip)
        data += write_ushort(port)
        data += b'\x09'  # 9 for stats, 1 for vanilla query to the server https://wiki.vg/Protocol#Status_Request
        handshake_packet = w_varint(len(data)) + data

        data: bytes = b'\x00'
        request_packet = w_varint(len(data)) + data
        sock.sendall(handshake_packet + request_packet)
        res_packet_length = r_varint(sock)
        if res_packet_length < 10:
            raise ValueError('invalid response')

        res_packet_id: bytes = sock.recv(1)
        print('packet id', ord(res_packet_id))
        length = r_varint(sock)
        data = b''
        while len(data) != length:
            chunk = sock.recv(length - len(data))
            if not chunk:
                raise ValueError('connection aborted')
            data += chunk

        print(json.dumps(json.loads(data.decode('utf-8')), indent=2))

    finally:
        sock.close()


if __name__ == '__main__':
    main()
