package io.github.kwisatzx.lastepoch.gui.tabcontrollers;

import io.github.kwisatzx.lastepoch.fileoperations.CharacterOperations;
import io.github.kwisatzx.lastepoch.fileoperations.Selectable;
import io.github.kwisatzx.lastepoch.gui.MainController;
import io.github.kwisatzx.lastepoch.itemdata.Affix;
import io.github.kwisatzx.lastepoch.itemdata.AffixList;
import io.github.kwisatzx.lastepoch.itemdata.Item;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public abstract class GuiTab {
    protected HashMap<String, TextField> textFields;
    protected HashMap<String, ChoiceBox<String>> choiceBoxes;
    protected HashMap<String, ComboBox<AffixDisplayer>> comboBoxes;
    protected TreeItem<Selectable> selectedItem;
    protected boolean characterSelected = false;
    protected CharacterOperations lastSelectedChr;
    protected boolean eventsLockedForReading = false;

    protected void tabInit(Pane rootPane) {
        textFields = new HashMap<>();
        choiceBoxes = new HashMap<>();
        comboBoxes = new HashMap<>();
        categorizeFieldsFromParentNode(rootPane);
        initAffixComboBoxes();
    }

    protected void receiveSelection(TreeItem<Selectable> treeItem) {
        this.selectedItem = treeItem;
        fillDataFields();
    }

    protected Optional<CharacterOperations> getCharaOp() {
        if (characterSelected) return Optional.ofNullable(selectedItem.getValue().getCharaOp());
        if (lastSelectedChr != null) return Optional.of(lastSelectedChr);
        if (selectedItem != null) return Optional.ofNullable(Item.getItemOwner(selectedItem.getValue().getItemObj()));
        else return Optional.empty();
    }

    protected void setEquipment() {
        getCharaOp().ifPresent(CharacterOperations::setEquipmentInFileString);
    }

    abstract protected void fillDataFields();

    protected void intToTextField(String textFieldName, int num) {
        textFields.get(textFieldName).setText(String.valueOf(num));
    }

    protected void categorizeFieldsFromParentNode(Pane parentNode) {
        for (Node node : parentNode.getChildren()) {
            if (node.getId() != null) {
                if (node instanceof TextField) {
                    textFields.put(node.getId(), (TextField) node);
                } else if (node instanceof ChoiceBox) {
                    choiceBoxes.put(node.getId(), (ChoiceBox<String>) node);
                } else if (node instanceof ComboBox) {
                    if (node.getId().contains("affix"))
                        comboBoxes.put(node.getId(), (ComboBox<AffixDisplayer>) node);
                }
            }
        }
    }

    protected void initAffixComboBoxes() {
        EventHandler<KeyEvent> preventClosingOnSpace = event -> {
            if (event.getCode() == KeyCode.SPACE) event.consume();
        };

        for (ComboBox<AffixDisplayer> comboBox : comboBoxes.values()) {
            comboBox.setVisibleRowCount(15);
            comboBox.getItems().addAll(AffixDisplayer.getList());

            comboBox.getEditor().setOnKeyReleased(keyEvent -> {
                KeyCode kc = keyEvent.getCode();
                if (kc.isLetterKey() || kc.isDigitKey() || kc == KeyCode.BACK_SPACE) {
                    comboBox.getItems().setAll(AffixDisplayer.getList(
                            AffixList.getListFromPartialName(comboBox.getEditor().getText())));
                    keyEvent.consume();
                }
            });

            ComboBoxListViewSkin<AffixDisplayer> eventSkin = new ComboBoxListViewSkin<>(comboBox);
            eventSkin.getPopupContent().addEventFilter(KeyEvent.ANY, preventClosingOnSpace);
            comboBox.setSkin(eventSkin);
        }
    }

    public void refreshTreeView() {
        MainController.getInstance().getTreeViewHandlerReference().refresh();
    }

    public void reloadTreeView() {
        MainController.getInstance().getTreeViewHandlerReference().refreshCharaItems();
    }

    static final class AffixDisplayer {
        private static String previewChoice = "For Tier 7";
        private final Affix affix;

        AffixDisplayer(Affix affix) {this.affix = affix;}

        public static List<GuiItemTab.AffixDisplayer> getList() {
            return AffixList.getList().stream()
                    .map(GuiTab.AffixDisplayer::new)
                    .toList();
        }

        public static List<GuiItemTab.AffixDisplayer> getList(List<Affix> affixList) {
            return affixList.stream()
                    .map(GuiTab.AffixDisplayer::new)
                    .toList();
        }

        public static void setPreviewChoice(String previewChoice) {
            GuiTab.AffixDisplayer.previewChoice = previewChoice;
        }

        public static Affix getAffixFromDisplayName(String name) {
            String filtered;
            if (name.contains("Idol Affix")) {
                filtered = name.substring(0, name.length() - " [Idol Affix]".length());
            } else filtered = name;
            for (Affix affix : AffixList.getList()) {
                for (String tierValue : affix.getTierValues()) {
                    if (tierValue.equals(filtered)) return affix;
                }
            }
            return null;
        }

        public Affix getAffix() {return affix;}

        @Override
        public String toString() {
            boolean oneTier = affix.getNumberOfTiers() == 1;
            String affixText;
            if (oneTier) affixText = affix.getTierValues()[0];
            else {
                switch (previewChoice) {
                    case "For Tier 7" -> affixText = affix.getTierValues()[6];
                    case "For Tier 6" -> affixText = affix.getTierValues()[5];
                    case "For Tier 5" -> affixText = affix.getTierValues()[4];
                    case "For Tier 4" -> affixText = affix.getTierValues()[3];
                    case "For Tier 3" -> affixText = affix.getTierValues()[2];
                    case "For Tier 2" -> affixText = affix.getTierValues()[1];
                    case "For Tier 1" -> affixText = affix.getTierValues()[0];
                    default -> affixText = affix.getName();
                }
            }
            if (oneTier) affixText += " [Idol Affix]";
            return affixText;
        }
    }
}
