package com.totsp.crossword;

import android.util.Log;

import com.totsp.crossword.puz.Box;
import com.totsp.crossword.puz.Puzzle;


public class Playboard {
    Position highlightLetter = new Position(0, 0);
    Puzzle puzzle;
    Box[][] boxes;
    boolean across = true;

    public Playboard(Puzzle puzzle) {
        this.puzzle = puzzle;
        this.highlightLetter = new Position(0, 0);
        this.boxes = new Box[puzzle.getBoxes()[0].length][puzzle.getBoxes().length];

        for (int x = 0; x < puzzle.getBoxes().length; x++) {
            for (int y = 0; y < puzzle.getBoxes()[x].length; y++) {
                boxes[y][x] = puzzle.getBoxes()[x][y];
            }
        }
    }

    public void setAcross(boolean across) {
        this.across = across;
    }

    public boolean isAcross() {
        return across;
    }

    public Box[][] getBoxes() {
        return this.boxes;
    }

    public Clue getClue() {
        Clue c = new Clue();
        Position start = this.getCurrentWordStart();
        c.number = this.getBoxes()[start.across][start.down].clueNumber;
        c.hint = this.across ? this.puzzle.findAcrossClue(c.number)
                             : this.puzzle.findDownClue(c.number);

        return c;
    }

    public Position getCurrentWordStart() {
        if (this.isAcross()) {
            int col = this.highlightLetter.across;
            Box b = null;

            while (b == null) {
                if (boxes[col][this.highlightLetter.down] != null && boxes[col][this.highlightLetter.down].across) {
                    b = boxes[col][this.highlightLetter.down];
                } else {
                    col--;
                }
            }

            return new Position(col, this.highlightLetter.down);
        } else {
            int row = this.highlightLetter.down;
            Box b = null;
            
            while (b == null) {
                if (boxes[this.highlightLetter.across][row] != null && boxes[this.highlightLetter.across][row].down) {
                    b = boxes[this.highlightLetter.across][row];
                } else {
                    row--;
                }
            }

            return new Position(this.highlightLetter.across, row);
        }
    }

    public Word setHighlightLetter(Position highlightLetter) {
    	Word w = this.getCurrentWord();
    	if(highlightLetter.equals(this.highlightLetter) ){
    		this.toggleDirection();
    	} else {
    		
    		if(this.boxes.length > highlightLetter.across && 
    				this.boxes[highlightLetter.across].length > highlightLetter.down &&
    				this.boxes[highlightLetter.across][highlightLetter.down] != null){
    			this.highlightLetter = highlightLetter;
    		}
    	}
    	return w;
    }

    public Position getHighlightLetter() {
        return highlightLetter;
    }
    
    public Word getCurrentWord(){
    	Word w = new Word();
    	w.start = this.getCurrentWordStart();
    	w.across = this.across;
    	w.length = this.getWordRange();
    	return w;
    }

    public int getWordRange() {
        Position start = this.getCurrentWordStart();

        if (this.isAcross()) {
            int col = start.across;
            Box b = null;

            do {
                b = null;

                int checkCol = col + 1;

                try {
                    col++;
                    b = this.getBoxes()[checkCol][start.down];
                } catch (RuntimeException e) {
                }
            } while (b != null);

            return col - start.across;
        } else {
            int row = start.down;
            Box b = null;

            do {
                b = null;

                int checkRow = row + 1;

                try {
                    row++;
                    b = this.getBoxes()[start.across][checkRow];
                } catch (RuntimeException e) {
                }
            } while (b != null);

            return row - start.down;
        }
    }

    public Word moveDown() {
    	Word w = this.getCurrentWord();
        Box b = null;
        int checkRow = this.highlightLetter.down;

        while ((b == null) && (checkRow < (this.getBoxes().length - 1))) {
            try {
                b = this.getBoxes()[this.highlightLetter.across][++checkRow];
            } catch (RuntimeException e) {
            }
        }

        this.highlightLetter = new Position(this.highlightLetter.across,
                checkRow);
        return w;
    }

    public Word moveLeft() {
    	Word w = this.getCurrentWord();
        Box b = null;
        int checkCol = this.highlightLetter.across;

        while ((b == null) && (checkCol > 0)) {
            try {
                b = this.getBoxes()[--checkCol][this.highlightLetter.down];
            } catch (RuntimeException e) {
            }
        }

        this.highlightLetter = new Position(checkCol, this.highlightLetter.down);
        return w;
    }

    public Word moveRight() {
    	Word w = this.getCurrentWord();
        Box b = null;
        int checkCol = this.highlightLetter.across;

        while ((b == null) &&
                (checkCol < (this.getBoxes()[this.highlightLetter.across].length -
                1))) {
            try {
                b = this.getBoxes()[++checkCol][this.highlightLetter.down];
            } catch (RuntimeException e) {
            }
        }

        this.highlightLetter = new Position(checkCol, this.highlightLetter.down);
        return w;
    }

    public Word movieUp() {
    	Word w = this.getCurrentWord();
        Box b = null;
        int checkRow = this.highlightLetter.down;

        while ((b == null) && (checkRow > 0)) {
            try {
                b = this.getBoxes()[this.highlightLetter.across][--checkRow];
            } catch (RuntimeException e) {
            }
        }

        this.highlightLetter = new Position(this.highlightLetter.across,
                checkRow);
        return w;
    }

    public Word nextLetter() {
        if(across){
        	return this.moveRight();
        } else {
        	return this.moveDown();
        }
    }
    
    public Word playLetter(char letter){
    	Box b = this.boxes[this.highlightLetter.across][this.highlightLetter.down];
    	b.response = letter;
    	return this.nextLetter();
    	
    }

    public Word toggleDirection() {
    	Word w = this.getCurrentWord();
        this.across = !across;
        return w;
    }

    public static class Clue {
        public String hint;
        public int number;
    }

    public static class Position {
        public int across;
        public int down;

        public Position(int across, int down) {
            this.down = down;
            this.across = across;
        }
        
        @Override
        public boolean equals(Object o) {
        	if(o.getClass() != this.getClass() ){
        		return false;
        	}
        	Position p = (Position) o;
        	return (p.down == this.down && p.across == this.across );
        }
        
        @Override
        public int hashCode(){
        	return this.across ^ this.down;
        }

        public String toString() {
            return "[" + this.across + " x " + this.down + "]";
        }
    }

    public static class Word {
        public Position start;
        public boolean across;
        public int length;
        
        public boolean checkInWord(int across, int down){
        	int ranging = this.across ? across : down;
            boolean offRanging = this.across
                ? (down == start.down) : (across == start.across);

            int startPos = this.across ? start.across : start.down;

            return (offRanging && (startPos <= ranging) &&
                    ((startPos + length) > ranging));
        }
    }
}