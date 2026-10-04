from pathlib import Path
import json
import shutil
root = Path(__file__).resolve().parents[1]
pack = root / 'build/smoke-run/resourcepacks/continuity-smoke'
assets = pack / 'assets/minecraft'
(assets/'optifine').mkdir(parents=True,exist_ok=True)
(assets/'textures/block').mkdir(parents=True,exist_ok=True)
(pack/'pack.mcmeta').write_text(json.dumps({'pack':{'pack_format':15,'description':'Continuity native smoke fixtures'}}))
(assets/'optifine/emissive.properties').write_text('suffix.emissive=_e\n')
(assets/'optifine/block.properties').write_text('layer.translucent=black_concrete\ndisableSolidCheck=true\n')
source=next((root/'src/main/resources/resourcepacks/default').rglob('0.png'))
shutil.copyfile(source,assets/'textures/block/gold_block_e.png')
for method,block,tiles,extra in [
    ('ctm_compact','iron_block',5,''), ('horizontal','copper_block',4,''),
    ('vertical','lapis_block',4,''), ('horizontal+vertical','emerald_block',7,''),
    ('vertical+horizontal','diamond_block',7,''), ('random','redstone_block',4,''),
    ('repeat','quartz_block',4,'width=2\nheight=2\n'), ('fixed','coal_block',1,''),
    ('overlay_fixed','stone',1,'layer=cutout\ntintIndex=0\ntintBlock=grass_block\n'),
]:
    folder=assets/'optifine/ctm'/block; folder.mkdir(parents=True,exist_ok=True)
    (folder/'test.properties').write_text(f'method={method}\nmatchBlocks={block}\ntiles=0-{tiles-1}\n'+extra)
    for i in range(tiles): shutil.copyfile(source,folder/f'{i}.png')
