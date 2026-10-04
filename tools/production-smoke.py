"""Run reobfuscated release jars using an already installed Forge 47.4.10 cache."""
import argparse
import json
from pathlib import Path
import shutil
import subprocess

root = Path(__file__).resolve().parents[1]
parser = argparse.ArgumentParser(description=__doc__)
parser.add_argument('--store', type=Path, required=True)
parser.add_argument('--embeddium', type=Path)
args = parser.parse_args()
store = args.store.resolve()
forge_id = '1.20.1-forge-47.4.10'
vanilla = json.loads((store/'versions/1.20.1/1.20.1.json').read_text())
forge = json.loads((store/f'versions/{forge_id}/{forge_id}.json').read_text())
libraries = store/'libraries'
run = root/'build'/('production-smoke-embeddium' if args.embeddium else 'production-smoke')
(run/'mods').mkdir(parents=True,exist_ok=True)
release = root/'build/libs/viking-continuity-forge-1.20.1-3.0.1-viking.1.jar'
harness = root/'build/libs/continuity-native-smoke-3.0.1-viking.1.jar'
for jar in (release,harness): shutil.copyfile(jar,run/'mods'/jar.name)
if args.embeddium: shutil.copyfile(args.embeddium,run/'mods'/args.embeddium.name)
shutil.copytree(root/'build/smoke-run/resourcepacks',run/'resourcepacks',dirs_exist_ok=True)
(run/'options.txt').write_text('resourcePacks:["vanilla","mod_resources","continuity/default","file/continuity-smoke"]\n')

def allowed(entry):
    permit = 'rules' not in entry
    for rule in entry.get('rules',[]):
        os = rule.get('os',{})
        if os.get('name','windows') == 'windows' and not rule.get('features'):
            permit = rule['action'] == 'allow'
    return permit

selected = {}
for entry in vanilla['libraries']+forge['libraries']:
    artifact = entry.get('downloads',{}).get('artifact')
    if artifact and allowed(entry):
        parts=entry['name'].split(':')
        selected[':'.join(parts[:2]+parts[3:])] = libraries/artifact['path']
classpath = ';'.join(str(path) for path in selected.values())
values = {'library_directory':str(libraries),'classpath_separator':';','version_name':forge_id}
jvm=[]
for value in forge['arguments']['jvm']:
    for key,replacement in values.items(): value=value.replace('${'+key+'}',replacement)
    jvm.append(value)
java='C:/Users/kerla/.jdks/ms-17.0.19/bin/java.exe'
command=[java,'-Xmx4G','-Djava.library.path='+str(store/'natives'),*jvm,'-DlegacyClassPath='+classpath,'-cp',classpath,forge['mainClass'],
    '--username','ContinuitySmoke','--version',forge_id,'--gameDir',str(run),'--assetsDir',str(store/'assets'),
    '--assetIndex',vanilla['assetIndex']['id'],'--uuid','00000000-0000-0000-0000-000000000001','--accessToken','0','--userType','legacy',*forge['arguments']['game']]
log=run/'launch.log'
with log.open('w',encoding='utf-8') as output:
    result=subprocess.run(command,cwd=run,stdout=output,stderr=subprocess.STDOUT,timeout=240)
content=log.read_text(encoding='utf-8',errors='replace')
for line in content.splitlines():
    if 'CONTINUITY_NATIVE_' in line: print(line)
if result.returncode or 'CONTINUITY_NATIVE_SMOKE_FAILED' in content or content.count('CONTINUITY_NATIVE_SMOKE_OK')!=2:
    raise SystemExit(f'Production smoke failed; see {log}')
print('CONTINUITY_PRODUCTION_OK forge=47.4.10 embeddium='+str(bool(args.embeddium)))
