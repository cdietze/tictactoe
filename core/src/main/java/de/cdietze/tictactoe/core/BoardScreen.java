package de.cdietze.tictactoe.core;

import de.cdietze.playn_util.ColorUtils;
import de.cdietze.playn_util.ScaledElement;
import de.cdietze.playn_util.Screen;
import playn.core.Canvas;
import playn.core.Image;
import playn.core.Sound;
import playn.scene.GroupLayer;
import playn.scene.ImageLayer;
import playn.scene.Layer;
import playn.scene.Pointer;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import react.Slot;
import react.Values;
import tripleplay.ui.*;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.ui.layout.BorderLayout;
import tripleplay.util.Layers;

import java.util.Iterator;

public class BoardScreen extends Screen {

    private final MainGame game;

    public BoardScreen(MainGame game) {
        super(game);
        this.game = game;
    }

    @Override
    public void wasAdded() {
        super.wasAdded();
        IDimension viewSize = plat.graphics().viewSize;

        final BoardState boardState = new BoardState();
        boardState.isOAi.update(true);
        attachAi(boardState);

        Board board = new Board(boardState);
        ScaledElement boardElement = new ScaledElement(board.layer, new Dimension(3f, 3f));
        boardElement.setStyles(Style.BACKGROUND.is(tripleplay.ui.Background.blank().inset(20f)));

        Root root = iface.createRoot(new BorderLayout(), SimpleStyles.newSheet(plat.graphics()), layer);
        root.setSize(viewSize);

        root.add(boardElement.setConstraint(BorderLayout.CENTER));

        boardState.gameState.connectNotify(new Slot<BoardState.GameState>() {
            @Override
            public void onEmit(BoardState.GameState gameState) {
                if (gameState == BoardState.GameState.RUNNING) return;
                createDialog(createGameOverPanel(boardState)).useShade().slideTopDown().duration(1000f).display();
                gameOver.play();
            }
        });
    }

    private Group createGameOverPanel(BoardState boardState) {
        /** Use the colors from {@link SimpleStyles} */
        int bgColor = 0xFFCCCCCC, ulColor = 0xFFEEEEEE, brColor = 0xFFAAAAAA;
        Group group = new Group(AxisLayout.vertical()).setStyles(Style.BACKGROUND.is(Background.roundRect(plat.graphics(), bgColor, 5, ulColor, 2).inset(20f)));
        group.add(new Label(getGameOverLabel(boardState.gameState.get())));
        group.add(new Button("New Game").onClick(new Slot<Button>() {
            @Override
            public void onEmit(Button event) {
                game.screens.replace(new BoardScreen(game));
            }
        }));
        return group;
    }

    private String getGameOverLabel(BoardState.GameState gameState) {
        switch (gameState) {
            case DRAW:
                return "It's a draw";
            case O_WON:
                return "O has won";
            case X_WON:
                return "X has won";
            default:
                return "";
        }
    }

    private void attachAi(final BoardState boardState) {
        Values.and(boardState.isOAi, Values.not(boardState.isXToMove)).connectNotify(new Slot<Boolean>() {
            @Override
            public void onEmit(Boolean event) {
                if (!event) return;
                int state = Ai.emptyState;
                for (int i = 0; i < 9; i++) {
                    BoardState.FieldType fieldType = boardState.fieldValue(i).get();
                    if (fieldType == BoardState.FieldType.EMPTY) continue;
                    state = Ai.setField(state, i, fieldType == BoardState.FieldType.X);
                }
                Ai.EvalResult result = Ai.eval(state);
                if (result.bestMoveIndex >= 0) {
                    boardState.tryToMark(result.bestMoveIndex);
                }
            }
        });
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
                                                        click1.play();
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

    private Sound click1 = plat.assets().getSound("sounds/click1");
    private Sound click2 = plat.assets().getSound("sounds/click2");
    private Sound gameOver = plat.assets().getSound("sounds/game_over");
}
