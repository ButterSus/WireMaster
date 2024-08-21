#!/usr/bin/env bash
find .idea -name "*.xml.template" | while IFS= read -r template; do
    config_file="${template%.template}"
    if [ ! -f "$config_file" ]; then
        cp "$template" "$config_file"
        echo "Copied $template to $config_file"
    fi
done
