package com.piera;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import it.uniroma1.di.tmancini.teaching.ai.search.Action;
import it.uniroma1.di.tmancini.teaching.ai.search.Problem;
import it.uniroma1.di.tmancini.teaching.ai.search.State;

public class ProteinFoldingState extends State {

    private char[][] grid; // the underlying grid: does not correspond to the "protein grid", since it must
                           // have indices from 0 to 2n-1
    private int prefixIdx; // the index of the last aminoacid that has been placed
    private int lastX; // the x coord of the last placed aminoacid
    private int lastY; // the y coord of the last placed aminoacid
    private int x; // the x coord of the head of the protein
    private int y; // the y coord of the head of the protein
    private int psize; // cached: the total size of the protein
    private int contacts; // cached: the number of H-H contacts

    private int hNotContacts; // heuristic: number of H not yet in contact

    public ProteinFoldingState(Problem p) {
        super(p);
        this.prefixIdx = -1;
        this.psize = ((ProteinFolding) p).getProtein().length;
        this.grid = new char[(((ProteinFolding) p).getSize() * 2) - 1][(((ProteinFolding) p).getSize() * 2) - 1];
        this.x = -1;
        this.y = -1;
        this.lastX = -1;
        this.lastY = -1;
        this.contacts = -1;
        this.hNotContacts = -1;
    }

    @Override
    public Collection<? extends Action> executableActions() {
        List<Action> result = new ArrayList<>();
        int gridSize = this.grid.length;

        char[] protein = ((ProteinFolding) getProblem()).getProtein();

        // if (prefixIdx + 1 >= protein.length) {
        // return result;
        // }
        char nextC = protein[prefixIdx + 1];

        // current real coordinates
        int currentRealX = (psize - 1) + x;
        int currentRealY = (psize - 1) + y;

        // check possible moves
        if (currentRealX - 1 >= 0 && grid[currentRealX - 1][currentRealY] == 'e') {
            result.add(new ProteinFoldingAction(this, x - 1, y, nextC));
        }

        if (currentRealX + 1 < gridSize && grid[currentRealX + 1][currentRealY] == 'e') {
            result.add(new ProteinFoldingAction(this, x + 1, y, nextC));
        }

        if (currentRealY - 1 >= 0 && grid[currentRealX][currentRealY - 1] == 'e') {
            result.add(new ProteinFoldingAction(this, x, y - 1, nextC));
        }

        if (currentRealY + 1 < gridSize && grid[currentRealX][currentRealY + 1] == 'e') {
            result.add(new ProteinFoldingAction(this, x, y + 1, nextC));
        }

        return result;
    }

    @Override
    public boolean isGoal() {
        return this.prefixIdx == this.psize - 1;
    }

    // Returns the resulting state after applying action a to this state
    public State resultingState(Action a) {
        ProteinFoldingState result = (ProteinFoldingState) this.clone();
        int size = result.grid.length;
        result.grid = new char[size][size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(this.grid[i], 0, result.grid[i], 0, size);
        }

        ProteinFoldingAction myAction = (ProteinFoldingAction) a;

        // apply the action
        int newX = myAction.x;
        int newY = myAction.y;
        char letter = myAction.c;

        result.set(newX, newY, letter);

        result.lastX = this.x;
        result.lastY = this.y;
        result.x = newX;
        result.y = newY;
        result.prefixIdx = this.prefixIdx + 1;

        int newContacts = result.getContacts();
        result.contacts = this.contacts + newContacts;

        result.hNotContacts = this.hNotContacts - newContacts;

        return result;
    }

    public static ProteinFoldingState newInitialState(ProteinFolding p) {
        ProteinFoldingState newState = new ProteinFoldingState(p);
        for (int x = -(newState.psize - 1); x <= newState.psize - 1; x++) {
            for (int y = -(newState.psize - 1); y <= newState.psize - 1; y++) {
                newState.set(x, y, 'e');
            }
        }

        newState.set(0, 0, p.getProtein()[0]);
        newState.x = 0;
        newState.y = 0;
        newState.prefixIdx = 0;
        newState.contacts = newState.getContacts();
        newState.hNotContacts = newState.psize / 2 - newState.getContacts();

        return newState;
    }

    /**
     * Gets the character at position (x,y) in the protein grid.
     * 
     */
    public char get(int x, int y) {
        return this.grid[psize - 1 + x][psize - 1 + y];
    }

    // Gets the underlying grid
    public char[][] getGrid() {
        return this.grid;
    }

    // Gets the total size of the protein
    public int getPsize() {
        return this.psize;
    }

    // Gets the x coordinate of the head of the protein
    public int getX() {
        return this.x;
    }

    // Gets the y coordinate of the head of the protein
    public int getY() {
        return this.y;
    }

    /**
     * Sets the character at position (x,y) in the protein grid.
     * c must be either "P" or "H" (we do not want to remove aminoacids!)
     */
    public void set(int x, int y, char c) {
        this.grid[psize - 1 + x][psize - 1 + y] = c;
    }

    // Calculates and returns the number of H-H contacts in the current state
    public int getContacts() {
        int psize = ((ProteinFolding) getProblem()).getSize();
        int gridSize = this.grid.length;

        int currentContacts = 0;

        // if no aminoacid has been placed yet
        char currentVal = ((ProteinFolding) getProblem()).getProtein()[prefixIdx];
        if (currentVal != 'H') {
            return currentContacts;
        }

        // current coordinates
        int realX = (psize - 1) + x;
        int realY = (psize - 1) + y;

        // coordinates of the last placed aminoacid
        int realLastX = (psize - 1) + lastX;
        int realLastY = (psize - 1) + lastY;

        // check neighbors
        int targetX = realX - 1;
        int targetY = realY;
        if (targetX >= 0) {
            if (grid[targetX][targetY] == 'H' && !(targetX == realLastX && targetY == realLastY)) {
                currentContacts++;
            }
        }

        targetX = realX + 1;
        targetY = realY;
        if (targetX < gridSize) {
            if (grid[targetX][targetY] == 'H' && !(targetX == realLastX && targetY == realLastY)) {
                currentContacts++;
            }
        }

        targetX = realX;
        targetY = realY - 1;
        if (targetY >= 0) {
            if (grid[targetX][targetY] == 'H' && !(targetX == realLastX && targetY == realLastY)) {
                currentContacts++;
            }
        }

        targetX = realX;
        targetY = realY + 1;
        if (targetY < gridSize) {
            if (grid[targetX][targetY] == 'H' && !(targetX == realLastX && targetY == realLastY)) {
                currentContacts++;
            }
        }

        this.contacts = currentContacts;
        return currentContacts;
    }

    // Returns the energy of the current state
    public int getEnergy() {
        return -contacts;
    }

    /*
     * =========================
     * 
     * Add equals(Object oo), hashCode(), clone() and toString()!
     * 
     * =========================
     */
    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!o.getClass().equals(this.getClass()))
            return false;
        ProteinFoldingState oo = (ProteinFoldingState) o;
        if (!(this.x == oo.x && this.y == oo.y))
            return false;

        if (this.hashCode() != oo.hashCode())
            return false;
        return Arrays.deepEquals(grid, oo.grid);
    }

    @Override
    public int hashCode() {
        return x + y + Arrays.deepHashCode(grid);
    }

    @Override
    public Object clone() {
        return super.clone();
    }

    public String toStringWithPrefix(String prefix) {
        int rows = grid.length;
        int cols = grid[0].length;

        StringBuilder s = new StringBuilder();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {

                if (grid[j][i] != 'e' && grid[j][i] != 0) {
                    s.append(grid[j][i]);
                } else {
                    s.append('.');
                }
                s.append(" ");
            }
            s.append("\n");
        }

        return s.toString();
    }

    @Override
    public String toString() {
        return toStringWithPrefix("");
    }

    @Override
    public double hValue() {
        return this.hNotContacts;
    }

}
