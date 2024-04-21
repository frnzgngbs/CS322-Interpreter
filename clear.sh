#!/bin/bash


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
