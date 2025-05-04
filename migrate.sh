#!/bin/bash

# Create new directories
mkdir -p ~/.iae/projects
mkdir -p ~/.iae/configurations

# Move existing projects if they exist
if [ -d "projects" ]; then
    cp -r projects/* ~/.iae/projects/
    echo "Projects migrated successfully"
fi

# Move existing configurations if they exist
if [ -d "configurations" ]; then
    cp -r configurations/* ~/.iae/configurations/
    echo "Configurations migrated successfully"
fi

echo "Migration complete!" 