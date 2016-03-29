package de.cdietze.tictactoe.core;

import de.cdietze.tictactoe.core.util.ColorUtils;
import de.cdietze.tictactoe.core.util.MyScreen;
import de.cdietze.tictactoe.core.util.ScaledElement;
import playn.scene.GroupLayer;
import playn.scene.Layer;
import playn.scene.Pointer;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import tripleplay.ui.Root;
import tripleplay.ui.SimpleStyles;
import tripleplay.ui.Style;
import tripleplay.ui.ToggleButton;
import tripleplay.ui.layout.BorderLayout;
import tripleplay.util.Layers;

import java.util.Iterator;

public class BoardScreen extends MyScreen {

    public BoardScreen(MainGame game) {
        super(game);
    }

    @Override
    public void wasAdded() {
        super.wasAdded();
        IDimension viewSize = game.plat.graphics().viewSize;

        Board board = new Board();
        ScaledElement boardElement = new ScaledElement(board.layer, new Dimension(3f, 3f));
        boardElement.setStyles(Style.BACKGROUND.is(tripleplay.ui.Background.blank().inset(20f)));

        Root root = iface.createRoot(new BorderLayout(), SimpleStyles.newSheet(game.plat.graphics()), layer);
        root.setSize(viewSize);

        root.add(boardElement.setConstraint(BorderLayout.CENTER));

        root.add(new ToggleButton("Toggle").setConstraint(BorderLayout.SOUTH));
    }

    private final class Board {
        public final GroupLayer layer = new GroupLayer();

        public Board() {
            layer.setSize(3, 3).setOrigin(Layer.Origin.CENTER);
            Iterator<Integer> colorGenerator = ColorUtils.uniqueColorGenerator(0f, .7f, .7f);
            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 3; x++) {
                    final int index = Position.toIndex(x, y);
                    GroupLayer fieldLayer = new GroupLayer();
                    fieldLayer.setSize(1, 1);
                    fieldLayer.events().connect(new Pointer.Listener() {
                                                    @Override
                                                    public void onStart(Pointer.Interaction iact) {
                                                        System.out.println("click on " + index);
                                                    }
                                                }
                    );
                    layer.addAt(fieldLayer, x, y);
                    Layer solid = Layers.solid(colorGenerator.next(), 1, 1);
                    fieldLayer.add(solid);
                }
            }
        }
    }

}
