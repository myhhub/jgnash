/*
 * jGnash, a personal finance application
 * Copyright (C) 2001-2019 Craig Cavanaugh
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jgnash.uifx.views.recurring;

import javafx.fxml.FXML;
import javafx.scene.control.SpinnerValueFactory;

import jgnash.engine.recurring.Reminder;
import jgnash.engine.recurring.YearlyReminder;
import jgnash.util.NotNull;

/**
 * Yearly repeating reminder controller.
 *
 * @author Craig Cavanaugh
 */
public class YearTabController extends AbstractTabController {

    @FXML
    void initialize() {
        super.initialize();

        reminder = new YearlyReminder();

        numberSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1, 1));
    }

    @Override
    public void setReminder(@NotNull final Reminder reminder) {
        if (!(reminder instanceof YearlyReminder)) {
            throw new RuntimeException("Incorrect Reminder type");
        }

        super.setReminder(reminder);
    }
}
