package com.ramitsuri.expensemanager.data.dummy;

import com.ramitsuri.expensemanager.entities.EditedSheet;

import java.util.ArrayList;
import java.util.List;

public class EditedSheets {
    public static List<EditedSheet> getEditedSheets() {
        List<EditedSheet> editedSheets = new ArrayList<>();

        EditedSheet editedSheet = new EditedSheet(32442);
        editedSheets.add(editedSheet);

        editedSheet = new EditedSheet(8743);
        editedSheets.add(editedSheet);

        editedSheet = new EditedSheet(3232);
        editedSheets.add(editedSheet);

        editedSheet = new EditedSheet(323214);
        editedSheets.add(editedSheet);

        editedSheet = new EditedSheet(78543);
        editedSheets.add(editedSheet);

        return editedSheets;
    }
}
