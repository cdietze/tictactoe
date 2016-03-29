package de.cdietze.tictactoe.core;

import de.cdietze.tictactoe.core.util.ColorUtils;
import de.cdietze.tictactoe.core.util.MyScreen;
import de.cdietze.tictactoe.core.util.ScaledElement;
import playn.core.Canvas;
import playn.core.Image;
import playn.scene.GroupLayer;
import playn.scene.ImageLayer;
import playn.scene.Layer;
import playn.scene.Pointer;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import react.Slot;
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

        Board board = new Board(new BoardState());
        ScaledElement boardElement = new ScaledElement(board.layer, new Dimension(3f, 3f));
        boardElement.setStyles(Style.BACKGROUND.is(tripleplay.ui.Background.blank().inset(20f)));

        Root root = iface.createRoot(new BorderLayout(), SimpleStyles.newSheet(game.plat.graphics()), layer);
        root.setSize(viewSize);

        root.add(boardElement.setConstraint(BorderLayout.CENTER));

        root.add(new ToggleButton("Toggle").setConstraint(BorderLayout.SOUTH));
    }

    private final class Board {
        public final GroupLayer layer = new GroupLayer();

        public Board(final BoardState boardState) {
            layer.setSize(3, 3).setOrigin(Layer.Origin.CENTER);
            Iterator<Integer> colorGenerator = ColorUtils.uniqueColorGenerator(0f, .5f, .7f);
            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 3; x++) {
                    final int fieldIndex = Position.toIndex(x, y);
                    final GroupLayer fieldLayer = new GroupLayer();
                    fieldLayer.setSize(1, 1);
                    fieldLayer.events().connect(new Pointer.Listener() {
                                                    @Override
                                                    public void onStart(Pointer.Interaction iact) {
                                                        System.out.println("click on " + fieldIndex);
                                                        boardState.tryToMark(fieldIndex);
                                                    }
                                                }
                    );
                    layer.addAt(fieldLayer, x, y);
                    Layer solid = Layers.solid(colorGenerator.next(), 1, 1);
                    fieldLayer.add(solid);
                    boardState.fieldValue(fieldIndex).connectNotify(new Slot<BoardState.FieldType>() {
                        Layer contentLayer = null;

                        @Override
                        public void onEmit(BoardState.FieldType fieldType) {
                            if (contentLayer != null) fieldLayer.remove(contentLayer);
                            contentLayer = null;
                            if (fieldType == BoardState.FieldType.EMPTY) return;
                            contentLayer = fieldType == BoardState.FieldType.X ? xImageLayer() : oImageLayer();
                            fieldLayer.addAt(contentLayer, .5f, .5f);
                        }
                    });
                }
            }
        }

        private ImageLayer oImageLayer() {
            ImageLayer imageLayer = new ImageLayer(circleImage);
            imageLayer.setSize(.8f, .8f).setOrigin(Layer.Origin.CENTER);
            return imageLayer;
        }

        private ImageLayer xImageLayer() {
            ImageLayer imageLayer = new ImageLayer(crossImage);
            imageLayer.setSize(.8f, .8f).setOrigin(Layer.Origin.CENTER);
            return imageLayer;
        }

        private Image circleImage = drawCircleImage();
        private Image crossImage = drawCrossImage();

        private Image drawCircleImage() {
            float size = 100;
            float radius = .45f * size;
            Canvas canvas = plat.graphics().createCanvas(size, size);
            canvas.setStrokeColor(0xff222222);
            canvas.setStrokeWidth(size / 20);
            canvas.strokeCircle(size / 2, size / 2, radius);
            return canvas.image;
        }

        private Image drawCrossImage() {
            float size = 100;
            float margin = .1f * size;
            Canvas canvas = plat.graphics().createCanvas(size, size);
            canvas.setStrokeColor(0xff222222);
            canvas.setStrokeWidth(size / 20);
            canvas.drawLine(margin, margin, size - margin, size - margin);
            canvas.drawLine(size - margin, margin, margin, size - margin);
            return canvas.image;
        }
    }
}
