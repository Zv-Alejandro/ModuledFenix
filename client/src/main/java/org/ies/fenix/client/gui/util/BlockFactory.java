package org.ies.fenix.client.gui.util;

import org.ies.fenix.client.gui.model.script.*;
import org.ies.fenix.client.gui.service.DragAndDropService;
import org.ies.fenix.client.gui.view.blocks.*;

public class BlockFactory {

    public static BaseBlockView createView(
            BaseBlockModel model,
            String type,
            DragAndDropService dragService
    ) {

        BaseBlockView view = switch (type) {

            case "text" -> (model == null)
                    ? new NarrativeBlockView()
                    : new NarrativeBlockView((NarrativeBlockModel) model);

            case "dialog" -> (model == null)
                    ? new DialogBlockView()
                    : new DialogBlockView((DialogBlockModel) model);

            case "background" -> (model == null)
                    ? new BackgroundBlockView()
                    : new BackgroundBlockView((BackgroundBlockModel) model);

            case "character_create" -> (model == null)
                    ? new CharacterCreateBlockView()
                    : new CharacterCreateBlockView((CharacterCreateBlockModel) model);

            case "decision" -> {
                DecisionBlockView v = (model == null)
                        ? new DecisionBlockView()
                        : new DecisionBlockView((DecisionBlockModel) model, dragService);

                v.setupContainerDragAndDrop(dragService);
                yield v;
            }

            case "option" -> (model == null)
                    ? new OptionBlockView()
                    : new OptionBlockView((OptionBlockModel) model);

            case "scene" -> {
                SceneBlockView v = (model == null)
                        ? new SceneBlockView()
                        : new SceneBlockView((SceneBlockModel) model, dragService);

                v.setupContainerDragAndDrop(dragService);
                yield v;
            }

            case "character" -> (model == null)
                    ? new CharacterBlockView()
                    : new CharacterBlockView((CharacterBlockModel) model);

            default -> throw new IllegalArgumentException("Unknown type: " + type);
        };

        // SOLO editor blocks tienen drag
        if (model != null) {
            view.setOnDragDetected(event -> {
                dragService.startDrag(view, event);
                event.consume();
            });
        }

        return view;
    }

    // ============================================================
    // EDITOR BLOCKS (CON MODELO)
    // ============================================================

    public static NarrativeBlockView createNarrative(NarrativeBlockModel model) {
        return new NarrativeBlockView(model);
    }

    public static DialogBlockView createDialog(DialogBlockModel model) {
        return new DialogBlockView(model);
    }

    public static BackgroundBlockView createBackground(BackgroundBlockModel model) {
        return new BackgroundBlockView(model);
    }

    public static CharacterCreateBlockView createCharacterCreate(CharacterCreateBlockModel model) {
        return new CharacterCreateBlockView(model);
    }

    public static DecisionBlockView createDecision(DecisionBlockModel model, DragAndDropService service) {
        DecisionBlockView v = new DecisionBlockView(model, service);
        v.setupContainerDragAndDrop(service);
        return v;
    }

    public static OptionBlockView createOption(OptionBlockModel model) {
        return new OptionBlockView(model);
    }

    public static SceneBlockView createScene(SceneBlockModel model, DragAndDropService service) {
        SceneBlockView v = new SceneBlockView(model, service);
        v.setupContainerDragAndDrop(service);
        return v;
    }

    public static CharacterBlockView createCharacter(CharacterBlockModel model) {
        return new CharacterBlockView(model);
    }

    // ============================================================
    // PALETTE BLOCKS (SIN MODELO)
    // ============================================================

    public static NarrativeBlockView createNarrativeBlock() {
        NarrativeBlockView v = new NarrativeBlockView();
        v.setPaletteBlock(true);
        return v;
    }

    public static DialogBlockView createShowBlock() {
        DialogBlockView v = new DialogBlockView();
        v.setPaletteBlock(true);
        return v;
    }

    public static SceneBlockView createSceneBlock() {
        SceneBlockView v = new SceneBlockView();
        v.setPaletteBlock(true);
        return v;
    }

    public static OptionBlockView createOptionBlock() {
        OptionBlockView v = new OptionBlockView();
        v.setPaletteBlock(true);
        return v;
    }

    public static DecisionBlockView createDecisionBlock() {
        DecisionBlockView v = new DecisionBlockView();
        v.setPaletteBlock(true);
        return v;
    }

    public static BackgroundBlockView createBackgroundBlock() {
        BackgroundBlockView v = new BackgroundBlockView();
        v.setPaletteBlock(true);
        return v;
    }

    public static CharacterCreateBlockView createCharacterCreateBlock() {
        CharacterCreateBlockView v = new CharacterCreateBlockView();
        v.setPaletteBlock(true);
        return v;
    }

    public static CharacterBlockView createCharacterBlock() {
        CharacterBlockView v = new CharacterBlockView();
        v.setPaletteBlock(true);
        return v;
    }
}