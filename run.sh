#!/bin/bash
# Navigate to the directory where this script lives
cd "$(dirname "$0")"

# Run your bundled Java program
java -jar target/*-jar-with-dependencies.jar

# Keeps the Bash window open so you can read the output before it closes
echo ""
read -p "Press Enter to close..."
