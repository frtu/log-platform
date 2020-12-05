echo "Type 'startdck' to start log-platform"
startdck() {
  echo "Make sure you have ** docker-compose ** installed !!"
  (cd infra/docker && exec docker-compose up)
}
