package UnitInfo.core;

import UnitInfo.shaders.LineShader;
import UnitInfo.shaders.RangeShader;
import arc.*;
import arc.net.Server;
import arc.util.Log;
import mindustry.*;
import mindustry.core.NetClient;
import mindustry.game.EventType.*;
import mindustry.gen.Call;
import mindustry.mod.*;

import static UnitInfo.SVars.*;
import static arc.Core.*;

public class Main extends Mod {

    @Override
    public void init(){
        turretRange = new RangeShader();
        lineShader = new LineShader();

        Core.app.post(() -> {
            Mods.ModMeta meta = Vars.mods.locateMod("unitinfo").meta;
            meta.displayName = "[#B5FFD9]Unit Information[]";
            meta.author = "[#B5FFD9]Sharlotte[lightgray]#0018[][]";
            meta.description = bundle.get("shar-description");
        });

        Events.on(ClientLoadEvent.class, e -> {
            new SettingS().init();
            hud = new HudUi();
            hud.addWaveTable();
            hud.addUnitTable();
            hud.addTable();
            hud.addWaveInfoTable();
            hud.addSchemTable();
            hud.setEvents();
            OverDrawer.setEvent();
            if(jsonGen) ContentJSON.save();
        });

        Events.on(WorldLoadEvent.class, e -> {
            hud = new HudUi();
            hud.addWaveTable();
        });

        Events.on(WaveEvent.class, e -> {
            Vars.ui.hudGroup.removeChild(hud.waveTable);
            hud = new HudUi();
            hud.addWaveTable();
        });
    }
}
