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
package jgnash.convert.importat.ofx;

/**
 * Utility class for handling OFX files with invalid characters
 */
class Sanitize {

    private Sanitize() {
        // utility class
    }


    /**
     * Replaces illegal XML characters with escaped characters
     *
     * @param xml String to process
     * @return valid string
     */
    static String sanitize(final String xml) {
        String ugly = xml;

        ugly = ugly.replaceAll("&(?!(?:amp);)", "&amp;");
        ugly = ugly.replaceAll("\"", "&quot;");
        ugly = ugly.replaceAll("'", "&apos;");

        return ugly;
    }
}
