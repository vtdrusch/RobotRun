package window;

import java.util.ArrayList;

import global.Fields;
import robot.RobotRun;
import screen.ScreenMode;
import screen.ScreenType;

public class MenuScroll {
	private final RobotRun robotRun;
	private final String name;
	private int maxDisp;
	private int xPos;
	private int yPos;
	
	private ArrayList<DisplayLine> contents;
	private boolean[] lineSelect;
	
	private int lineIdx;
	private int columnIdx;
	private int renderStart;
	
	public MenuScroll(RobotRun r, String n, int max, int x, int y) {
		robotRun = r;
		name = n;
		
		maxDisp = max;
		xPos = x;
		yPos = y;
		
		contents = new ArrayList<DisplayLine>();
		
		lineIdx = 0;
		columnIdx = 0;
	}
	
	public DisplayLine addLine(String... lineTxt) {
		contents.add(newLine(lineTxt));
		return contents.get(contents.size() - 1);
	}
	
	public DisplayLine addLine(int idx, String... lineTxt) {
		DisplayLine newLine = newLine(idx, lineTxt);
		contents.add(newLine);
		return newLine;
	}
	
	public void clear() {
		contents.clear();
	}
	
	public void drawLines(ScreenMode screen) {
		boolean selectMode = false;
		if(screen.getType() == ScreenType.TYPE_LINE_SELECT) { selectMode = true; } 
		
		if(contents.size() > 0) {
			lineIdx = RobotRun.clamp(lineIdx, 0, contents.size() - 1);
			columnIdx = RobotRun.clamp(columnIdx, 0, contents.get(lineIdx).size() - 1);
			renderStart = RobotRun.clamp(renderStart, lineIdx - (maxDisp - 1), lineIdx);
		}
		
		int next_px = 0, next_py = 0;
		int bg, txt, itemNo = 0;
		
		for(int i = renderStart; i < contents.size() && i - renderStart < maxDisp; i += 1) {
			//get current line
			DisplayLine temp = contents.get(i);
			next_px += temp.getxAlign();

			if(i == lineIdx) { bg = Fields.UI_DARK; }
			else             { bg = Fields.UI_LIGHT;}

			//if(i == 0 || contents.get(i - 1).itemIdx != contents.get(i).itemIdx) {
			//  //leading row select indicator []
			//  cp5.addTextarea(Integer.toString(index_contents))
			//  .setText("")
			//  .setPosition(next_px, next_py)
			//  .setSize(10, 20)
			//  .setColorBackground(bg)
			//  .hideScrollbar()
			//  .moveTo(g1);
			//}

			//index_contents++;
			//next_px += 10;
			
			//draw each element in current line
			for(int j = 0; j < temp.size(); j += 1) {
				if(i == lineIdx) {
					if(j == columnIdx && !selectMode){
						//highlight selected row + column
						txt = Fields.UI_LIGHT;
						bg = Fields.UI_DARK;          
					} 
					else if(selectMode && lineSelect != null && !lineSelect[temp.getItemIdx()]) {
						//highlight selected line
						txt = Fields.UI_LIGHT;
						bg = Fields.UI_DARK;
					}
					else {
						txt = Fields.UI_DARK;
						bg = Fields.UI_LIGHT;
					}
				} else if(selectMode && lineSelect != null && lineSelect[temp.getItemIdx()]) {
					//highlight any currently selected lines
					txt = Fields.UI_LIGHT;
					bg = Fields.UI_DARK;
				} else {
					//display normal row
					txt = Fields.UI_DARK;
					bg = Fields.UI_LIGHT;
				}

				//grey text for comme also this
				if(temp.size() > 0 && temp.get(0).contains("//")) {
					txt = robotRun.color(127);
				}

				robotRun.getCp5().addTextarea(name + itemNo)
				.setText(temp.get(j))
				.setFont(RobotRun.fnt_con14)
				.setPosition(xPos + next_px, yPos + next_py)
				.setSize(temp.get(j).length()*Fields.CHAR_WDTH + Fields.TXT_PAD, 20)
				.setColorValue(txt)
				.setColorBackground(bg)
				.hideScrollbar()
				.moveTo(robotRun.g1);

				next_px += temp.get(j).length()*Fields.CHAR_WDTH + (Fields.TXT_PAD - 8);
				itemNo += 1;
			}//end draw line elements

			if(i == lineIdx) { txt = Fields.UI_DARK;  }
			else             { txt = Fields.UI_LIGHT; }

			////Trailing row select indicator []
			//cp5.addTextarea(Integer.toString(index_contents))
			//.setText("")
			//.setPosition(next_px, next_py)
			//.setSize(10, 20)
			//.setColorBackground(txt)
			//.hideScrollbar()
			//.moveTo(g1);

			next_px = 0;
			next_py += 20;
			//index_contents += 1;
		}//end display contents
	}
	
	public DisplayLine get(int i) {
		return contents.get(i);
	}
	
	public ArrayList<DisplayLine> getContents() {
		return contents;
	}
	
	public int getColumnIdx() {
		return columnIdx;
	}
	
	public int getItemIdx() {
		if(lineIdx < contents.size() && lineIdx >= 0)
			return contents.get(lineIdx).getItemIdx();
		else
			return -1;
	}
	
	public int getLineIdx() {
		return lineIdx;
	}
	
	public int getRenderStart() {
		return renderStart;
	}
	
	public int getSelectedIdx() {
		int idx = columnIdx;
		for(int i = lineIdx - 1; i >= 0; i -= 1) {
			if(contents.get(i).getItemIdx() != contents.get(i + 1).getItemIdx()) break;
			idx += contents.get(i).size();
		}

		return idx;
	}
	
	public boolean[] getSelection() {
		return lineSelect;
	}
	
	public boolean isSelected(int idx) {
		return lineSelect[idx];
	}
	
	/**
	 * 
	 */
	public int moveDown(boolean page) {
		int size = contents.size();  

		if (page && size > (renderStart + maxDisp)) {
			// Move display frame down an entire screen's display length
			lineIdx = Math.min(size - 1, lineIdx + (maxDisp - 1));
			renderStart = (Math.max(0, Math.min(size - maxDisp, renderStart + (maxDisp - 1))));
		} else {
			// Move down a single row
			lineIdx = Math.min(size - 1, lineIdx + 1);
		}

		return getItemIdx();
	}

	public int moveLeft() {
		if(lineIdx > 0 && contents.get(lineIdx - 1).getItemIdx() == contents.get(lineIdx).getItemIdx()) {
			columnIdx = (columnIdx - 1);
			if(columnIdx < 0) {
				moveUp(false);
				columnIdx = (contents.get(lineIdx).size() - 1);
			}
		} else {
			columnIdx = (Math.max(0, columnIdx - 1));
		}
		
		return getSelectedIdx();
	}

	public int moveRight() {
		if(lineIdx < contents.size() - 1 && contents.get(lineIdx + 1).getItemIdx() == contents.get(lineIdx).getItemIdx()) {
			columnIdx = (columnIdx + 1);
			if(columnIdx > contents.get(lineIdx).size() - 1) {
				moveDown(false);
				columnIdx = (0);
			}
		} else {
			columnIdx = (Math.min(contents.get(lineIdx).size() - 1, columnIdx + 1));
		}
		
		return getSelectedIdx();
	}

	/**
	 * 
	 */
	public int moveUp(boolean page) {
		if (page && renderStart > 0) {
			// Move display frame up an entire screen's display length
			lineIdx = (Math.max(0, lineIdx - (maxDisp - 1)));
			renderStart = (Math.max(0, renderStart - (maxDisp - 1)));
		} 
		else {
			// Move up a single row
			lineIdx = (Math.max(0, lineIdx - 1));
		}

		return getItemIdx();
	}
	
	public DisplayLine newLine(String... columns) {
		DisplayLine line =  new DisplayLine();

		for(String col : columns) {
			line.add(col);
		}

		return line;
	}
	
	public DisplayLine newLine(int itemIdx, String... columns) {
		DisplayLine line =  new DisplayLine(itemIdx);

		for(String col : columns) {
			line.add(col);
		}

		return line;
	}
	
	public void reset() {
		lineIdx = 0;
		columnIdx = 0;
		renderStart = 0;
	}
	
	// clears the array of selected lines
	public boolean[] resetSelection(int n) {
		lineSelect = new boolean[n];
		return lineSelect;
	}
	
	public DisplayLine set(int i, DisplayLine d) {
		return contents.set(i, d);
	}
	
	public MenuScroll setLocation(int x, int y) {
		xPos = x;
		yPos = y;
		
		return this;
	}
	
	public MenuScroll setMaxDisplay(int max) {
		maxDisp = max;
		return this;
	}
	
	public MenuScroll setContents(ArrayList<DisplayLine> c) {
		contents = c;
		return this;
	}
	
	public void setColumnIdx(int i) {
		columnIdx = i;
	}

	public void setLineIdx(int i) {
		lineIdx = i;
	}
	
	public int size() {
		return contents.size();
	}
	
	public boolean toggleSelect(int idx) {
		return lineSelect[idx] = !lineSelect[idx];
	}
}
