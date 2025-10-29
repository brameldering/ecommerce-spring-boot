for f in ~/.gradle/caches/modules-2/files-2.1////.jar; do
  if zipinfo -1 "$f" | grep -q "HateoasHalProvider.class"; then
    echo "$f"
  fi
done