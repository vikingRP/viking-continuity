"""Check release metadata, generated mixin refmap and absence of external API classes."""
from pathlib import Path
import json
import zipfile

root=Path(__file__).resolve().parents[1]
jar=root/'build/libs/viking-continuity-forge-1.20.1-3.0.1-viking.1.jar'
with zipfile.ZipFile(jar) as archive:
    assert archive.testzip() is None
    names=set(archive.namelist())
    assert 'fabric.mod.json' not in names
    metadata=archive.read('META-INF/mods.toml').decode()
    assert 'modId="continuity"' in metadata
    assert 'connectormod' not in metadata and 'fabric_api' not in metadata
    refmap=json.loads(archive.read('continuity.refmap.json'))
    assert refmap['mappings']
    for name in names:
        if name.endswith('.class'):
            data=archive.read(name)
            assert b'net/fabricmc/' not in data,name
            assert b'com/llamalad7/' not in data,name
            assert b'com/terraformersmc/' not in data,name
            assert b'grondag/canvas/' not in data,name
    assert not any('/smoke/' in name for name in names)
    assert 'LICENSE' in names
print('CONTINUITY_RELEASE_OK runtime_external_apis=0 refmap=true smoke_classes=0')
