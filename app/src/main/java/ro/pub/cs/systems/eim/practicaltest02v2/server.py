import socket
from datetime import datetime
import time

def start_server():
    host = "127.0.0.1"  # Adresa locală (localhost)
    port = 12345        # Portul pe care serverul ascultă

    # Creare socket
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_socket.bind((host, port))
    server_socket.listen(1)  # Serverul acceptă o conexiune la un moment dat

    print(f"Server is running on {host}:{port}")
    conn, addr = server_socket.accept()
    print(f"Connected by {addr}")

    try:
        while True:
            current_time = datetime.now().strftime("%H:%M:%S")
            conn.sendall(current_time.encode('utf-8'))
            time.sleep(1)
    except (BrokenPipeError, KeyboardInterrupt):
        print("Connection closed.")
    finally:
        conn.close()
        server_socket.close()

if __name__ == "__main__":
    start_server()