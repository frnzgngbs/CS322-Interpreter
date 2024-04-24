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



#---------------------Deleting Logic

# Directory containing compiled .class files
class_dir="./src"

# Function to handle errors
handle_error() {
    echo "An error occurred:"
    # You can add additional error handling logic here
}

# Delete .class files
echo "Deleting .class files in $class_dir"
if find "$class_dir" -type f -name "*.class" -delete; then
    echo "Successfully deleted .class files"
else
    handle_error "Failed to delete .class files"
fi

read