#! /usr/bin/bash

set -e

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

echo "Copying FlowableDelegates..."
cp "$SCRIPT_DIR/../src/FlowableDelegates/target/FlowableDelegates-1.0-SNAPSHOT.jar" "$SCRIPT_DIR/Flowable"

echo "Building containers..."
docker-compose build

echo "...done"
