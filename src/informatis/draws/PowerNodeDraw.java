package informatis.draws;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import mindustry.core.*;
import mindustry.gen.*;
import mindustry.graphics.*;

import static arc.Core.atlas;
import static informatis.SUtils.getTarget;
import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class PowerNodeDraw extends OverDraw {
    Seq<Building> linkedNodes = new Seq<>();

    public PowerNodeDraw() {
        super("powerNode", OverDrawCategory.Link);
    }

    @Override
    public void draw() {
        if(getTarget() instanceof Building build) {
            linkedNodes.clear();
            drawNodeLink(build);
        }
    }

    IntSeq getPowerLinkedBuilds(Building build) {
        IntSeq seq = new IntSeq(build.power.links);
        seq.addAll(build.proximity().mapInt(Building::pos));
        return seq;
    }

    void drawNodeLink(Building node) {
        if(node.power == null) return;
        if(!linkedNodes.contains(node)) {
            linkedNodes.add(node);
            int[] builds = getPowerLinkedBuilds(node).items;
            for(int i : builds) {
                Building other = world.build(i);
                if(other == null || other.power == null) return;
                float angle1 = Angles.angle(node.x, node.y, other.x, other.y),
                        vx = Mathf.cosDeg(angle1), vy = Mathf.sinDeg(angle1),
                        len1 = node.block.size * tilesize / 2f - 1.5f, len2 = other.block.size * tilesize / 2f - 1.5f;
                Draw.color(Color.white, Color.valueOf("98ff98"), (1f - node.power.graph.getSatisfaction()) * 0.86f + Mathf.absin(3f, 0.1f));
                Draw.alpha(Renderer.laserOpacity);
                Drawf.laser(atlas.find("informatis-Slaser"), atlas.find("informatis-Slaser"), atlas.find("informatis-Slaser-end"), node.x + vx * len1, node.y + vy * len1, other.x - vx * len2, other.y - vy * len2, 0.25f);

                drawNodeLink(other);
            }
        }
    }
}
