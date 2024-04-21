#!/bin/bash

# Compile Java files
javac src/**/*.java

# Function to handle errors
handle_error() {
    echo -"An error occurred: Fail"
    # You can add additional error handling logic here
}

# Test cases directory
test_case_dir="./testcase"

# Retrieve all files from the testcase folder
test_cases=("$test_case_dir"/*)

# Loop through test cases
for testcase in "${test_cases[@]}"; do
    echo "Running test case: $testcase"
    if java ./src/Main.java "$testcase"; then
        echo "-------------------------------------Test case $testcase succeeded"
    else
        handle_error "Test case $testcase failed"
    fi
done
read