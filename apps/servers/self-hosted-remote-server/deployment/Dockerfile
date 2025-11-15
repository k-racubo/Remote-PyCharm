FROM python:3.11-slim
WORKDIR /app

RUN openssl req -newkey rsa:2048 -nodes \
    -keyout key.pem -x509 -days 365 \
    -out cert.pem -subj "/CN=0.0.0.0"

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY src/ ./src/

EXPOSE 8765

CMD ["python", "-m", "uvicorn", "src.main:app", \
    "--host", "0.0.0.0", "--port", "8765", \
    "--ssl-keyfile", "key.pem", "--ssl-certfile", "cert.pem"]
