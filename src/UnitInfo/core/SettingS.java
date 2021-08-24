package UnitInfo.core;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.scene.Group;
import arc.scene.event.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.scene.ui.layout.Stack;
import arc.struct.*;
import arc.util.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class SettingS {
    public SettingsMenuDialog.SettingsTable sharset;

    public void addGraphicSlideSetting(String key, int def, int min, int max, int step, SettingsMenuDialog.StringProcessor s, Seq<SharSetting> list){
        list.add(new SharSetting(key, def) {

            @Override
            public void add(Table table){
                Label value = new Label("");
                value.setStyle(Styles.outlineLabel);
                value.touchable = Touchable.disabled;
                value.setAlignment(Align.center);
                value.setWrap(true);

                Slider slider = new Slider(min, max, step, false);
                slider.setValue(settings.getInt(name));
                slider.changed(() -> {
                    settings.put(name, (int)slider.getValue());
                    value.setText(title + ": " + s.get((int)slider.getValue()));
                });
                slider.change();

                table.stack(slider, value).width(Math.min(Core.graphics.getWidth() / 1.2f, 460f)).row();
            }
        });
    }

    public void addGraphicCheckSetting(String key, boolean def, Seq<SharSetting> list){
        list.add(new SharSetting(key, def) {

            @Override
            public void add(Table table) {
                CheckBox box = new CheckBox(title);
                box.update(() -> box.setChecked(settings.getBool(name)));
                box.changed(() -> settings.put(name, box.isChecked()));

                table.add(box).row();
            }
        });
    }

    public void addGraphicTypeSetting(String key, float min, float max, int def, boolean integer, Boolp condition, Func<String, String> h, Seq<SharSetting> list){
        list.add(new SharSetting(key, def) {

            @Override
            public void add(Table settingsTable) {
                final String[] str = {""};
                settingsTable.table(t -> {
                    t.add(new Label(title + ": ")).left().padRight(5)
                        .update(a -> a.setColor(condition.get() ? Color.white : Color.gray));

                    t.field((integer ? settings.getInt(key) : settings.getFloat(key)) + str[0], s -> {
                        settings.put(key, integer ? Strings.parseInt(s) : Strings.parseFloat(s));
                        str[0] = h.get(s);
                    }).update(a -> a.setDisabled(!condition.get()))
                        .valid(f -> Strings.canParsePositiveFloat(f) && Strings.parseFloat(f) >= min && Strings.parseFloat(f) <= max).width(120f).left();
                });
                settingsTable.row();
            }
        });
    }

    public void init(){
        BaseDialog dialog = new BaseDialog("UnitInfo Setting");
        dialog.addCloseButton();
        sharset = new SettingsMenuDialog.SettingsTable();
        dialog.cont.center().add(sharset);
        ui.settings.shown(() -> {
            Table settingUi = (Table)((Group)((Group)(ui.settings.getChildren().get(1))).getChildren().get(0)).getChildren().get(0); //This looks so stupid lol - lmfao
            settingUi.row();
            settingUi.button(bundle.get("setting.shar-title"), Styles.cleart, dialog::show);
        });


        Seq<Seq<SharSetting>> settingSeq = new Seq<>();
        Seq<SharSetting> tapSeq = new Seq<>();
        addGraphicCheckSetting("infoui", true, tapSeq);
        addGraphicCheckSetting("weaponui", true, tapSeq);
        addGraphicTypeSetting("wavemax", 0, 100,200, true, () -> true, s -> s + "waves", tapSeq);
        addGraphicCheckSetting("pastwave", false, tapSeq);
        addGraphicCheckSetting("emptywave", true, tapSeq);
        addGraphicSlideSetting("barstyle", 0, 0, 5, 1, s -> s == 0 ? "default bar" : s + "th bar", tapSeq);
        addGraphicSlideSetting("infoUiScale", 100, 50, 100, 5, s -> s + "%", tapSeq);
        addGraphicSlideSetting("coreItemCheckRate", 60, 6, 180, 6, s -> Strings.fixed(s/60f,1) + "sec", tapSeq);
        addGraphicCheckSetting("allTeam", false, tapSeq);

        Seq<SharSetting> rangeSeq = new Seq<>();
        addGraphicTypeSetting("rangeRadius", 0, 50, 20, true, () -> true, s -> s + "tiles", rangeSeq);
        addGraphicCheckSetting("rangeNearby", true, rangeSeq);
        addGraphicCheckSetting("allTeamRange", false, rangeSeq);
        addGraphicCheckSetting("allTargetRange", false, rangeSeq);
        addGraphicCheckSetting("coreRange", false, rangeSeq);
        addGraphicCheckSetting("unitRange", false, rangeSeq);
        addGraphicCheckSetting("softRangeDrawing", false, rangeSeq);

        Seq<SharSetting> opacitySeq = new Seq<>();
        addGraphicSlideSetting("selectopacity", 50, 0, 100, 5, s -> s + "%", opacitySeq);
        addGraphicSlideSetting("baropacity", 50, 0, 100, 5, s -> s + "%", opacitySeq);
        addGraphicSlideSetting("uiopacity", 50, 0, 100, 5, s -> s + "%", opacitySeq);
        addGraphicSlideSetting("softRangeOpacity", 10, 0, 25, 1, s -> s + "%", opacitySeq);

        Seq<SharSetting> drawSeq = new Seq<>();
        addGraphicCheckSetting("gaycursor", false, drawSeq);
        addGraphicCheckSetting("unithealthui", true, drawSeq);
        addGraphicCheckSetting("linkedMass", true, drawSeq);
        addGraphicCheckSetting("linkedNode", false, drawSeq);
        addGraphicCheckSetting("select", true, drawSeq);
        addGraphicCheckSetting("deadTarget", false, drawSeq);
        addGraphicCheckSetting("distanceLine", true, drawSeq);
        addGraphicSlideSetting("unitlinelimit", 50, 50, 250, 10, s -> s + "units", drawSeq);

        Seq<SharSetting> etcSeq = new Seq<>();
        addGraphicCheckSetting("autoShooting", false, etcSeq);
        settingSeq.add(tapSeq, rangeSeq, opacitySeq);
        settingSeq.add(drawSeq, etcSeq);

        sharset.table(t -> {
            Seq<Button> buttons = new Seq<>();
            buttons.add(new TextButton(bundle.get("setting.shar-wave"), Styles.clearToggleMenut));
            buttons.add(new TextButton(bundle.get("setting.shar-range"), Styles.clearToggleMenut));
            buttons.add(new TextButton(bundle.get("setting.shar-opacity"), Styles.clearToggleMenut));
            buttons.add(new TextButton(bundle.get("setting.shar-draw"), Styles.clearToggleMenut));
            buttons.add(new TextButton(bundle.get("setting.shar-etc"), Styles.clearToggleMenut));
            buttons.each(b -> b.clicked(() -> buttons.each(b1 -> b1.setChecked(b1 == b))));
            t.table(Styles.black8, bt -> {
                bt.top().align(Align.top);
                buttons.each(b -> {
                    bt.add(b).minHeight(60f).minWidth(150f).top();
                });
            }).grow().row();

            Stack stack = new Stack();
            for(int i = 0; i < settingSeq.size; i++){
                int finalI = i;
                stack.add(new Table(st -> {
                    st.center();
                    for(SharSetting setting : settingSeq.get(finalI)) {
                        Label label = new Label(setting.description);
                        label.setColor(Color.gray);
                        label.setFontScale(0.75f);

                        st.table(stt -> {
                            stt.center();
                            setting.add(stt);
                            stt.add(label);
                        }).row();
                    }

                    st.button(Core.bundle.get("settings.reset", "Reset to Defaults"), () -> {
                        for(SharSetting setting : settingSeq.get(finalI)) {
                            if(setting.name != null && setting.title != null) {
                                Core.settings.put(setting.name, Core.settings.getDefault(setting.name));
                            }
                        }
                    }).margin(14.0F).width(240.0F).pad(6.0F);
                    st.visibility = () -> buttons.get(finalI).isChecked();
                    st.pack();
                }));
            }
            t.add(stack);
        });
    }
}
