package com.piera;

import it.uniroma1.di.tmancini.teaching.ai.search.Action;

public class ProteinFoldingAction extends Action {

    ProteinFoldingState state;
    int x;
    int y;
    char c;

    public ProteinFoldingAction(ProteinFoldingState state, int x, int y, char c) {
        // l'azione che aggiunge c (con c in {"H", "P"}) in posizione (x,y)
        this.state = state;
        this.x = x;
        this.y = y;
        this.c = c;
    }

    @Override
    public double getCost() {
        // calcolare il costo (Attenzione!)
        // può essere negativo...
        if (this.c != 'H') {
            return 0;
        }

        int newContacts = countNewContacts();
        return (this.state.getPsize() / 2) + (-1.0 * newContacts);
    }

    // Counts the number of new H-H contacts formed by placing the aminoacid at (x,
    // y)
    public int countNewContacts() {
        int newContacts = 0;

        char[][] grid = this.state.getGrid();
        int size = grid.length;

        int realX = (this.state.getPsize() - 1) + x;
        int realY = (this.state.getPsize() - 1) + y;

        int prevX = this.state.getX();
        int prevY = this.state.getY();

        // check left
        if (realX - 1 >= 0) {
            if (grid[realX - 1][realY] == 'H' && !(prevX == x - 1 && prevY == y)) {
                newContacts += 2;
            }
        }

        // check right
        if (realX + 1 < size) {
            if (grid[realX + 1][realY] == 'H' && !(prevX == x + 1 && prevY == y)) {
                newContacts += 2;
            }
        }

        // check up
        if (realY - 1 >= 0) {
            if (grid[realX][realY - 1] == 'H' && !(prevX == x && prevY == y - 1)) {
                newContacts += 2;
            }
        }

        // check down
        if (realY + 1 < size) {
            if (grid[realX][realY + 1] == 'H' && !(prevX == x && prevY == y + 1)) {
                newContacts += 2;
            }
        }

        if (grid[realX][realY + 1] == 'F') {
            newContacts += 1;
        }

        return newContacts;
    }

    /*
     * =========================
     * 
     * Add equals(Object oo), hashCode(), clone() and toString()!
     * 
     * =========================
     */
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!o.getClass().equals(this.getClass()))
            return false;
        ProteinFoldingAction oo = (ProteinFoldingAction) o;
        return this.state == oo.state && this.x == oo.x && this.y == oo.y && this.c == oo.c;
    }

    public int hashCode() {
        return state.hashCode() + x * 7 + y * 11 + c * 17;
    }

    public String toString() {
        return "Add " + c + " at (" + x + "," + y + ")";
    }

}
