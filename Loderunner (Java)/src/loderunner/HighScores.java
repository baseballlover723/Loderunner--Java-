package loderunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class HighScores {
	private File highScoreFile;
	private BufferedReader reader;
	private ArrayList<Integer> highScoreArrayList;
	private String[] highScoreLine;
	private JFileChooser chooser;
	private JFrame frame;
 
	public HighScores(JFrame frame) {
		this.highScoreFile = new File("src/High Score.txt");
		this.frame = frame;
		internalLoader();
	}

	/**
	 * a loader that is solely used for internal purposes
	 * its the same as load() but it doesn't return a value 
	 */
	private void internalLoader() {
		this.highScoreArrayList = new ArrayList<Integer>();
		try {
			this.reader = new BufferedReader(new FileReader(this.highScoreFile));
			String score;
			while ((score = reader.readLine()) != null) {
				// if not an empty line
				if (!score.equals("")) {
					// parses the line to a number if possible, if not, throws
					// an error
					this.highScoreArrayList.add(Integer.parseInt(score));
				}
			}
			checkOrder();

		} catch (FileNotFoundException e) {
			handleFileNotFoundException(e);
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * returns the high score table, parsing the high score table into an array list of integers
	 * it ignores empty lines
	 * @return ArrayList<Integer>
	 */
	public ArrayList<Integer> load() {
		this.highScoreArrayList = new ArrayList<Integer>();
		try {
			this.reader = new BufferedReader(new FileReader(this.highScoreFile));
			String score;
			while ((score = reader.readLine()) != null) {
				// if not an empty line
				if (!score.equals("")) {
					// parses the line to a number if possible, if not, throws
					// an error
					this.highScoreArrayList.add(Integer.parseInt(score));
				}
			}
			checkOrder();

		} catch (FileNotFoundException e) {
			handleFileNotFoundException(e);
			//internalLoader();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return this.highScoreArrayList;
	}

	/**
	 * checks the order the high score list is in and corrects the list so its
	 * in descending order
	 * 
	 * @throws FileNotFoundException
	 */
	private void checkOrder() throws FileNotFoundException {
		// goes through the list and only sorts if any element is out of order
		for (int k = this.highScoreArrayList.size() - 1; k > 0; k--) {
			if (this.highScoreArrayList.get(k - 1) < this.highScoreArrayList.get(k)) {
				// if out of order, sort ascending and then reverse
				Collections.sort(this.highScoreArrayList);
				Collections.reverse(this.highScoreArrayList);

				// overwrites the high score file
				overwrite();
				return;
			}
		}
	}

	/**
	 * overwrites the high score file
	 */
	private void overwrite() throws FileNotFoundException {
		try {
			PrintWriter writer = new PrintWriter(this.highScoreFile);
			try {
				for (int number : this.highScoreArrayList) {
					writer.println(number);
				}
			} finally {
				writer.close();
			}
		} catch (IOException e) {
			String msg = "Unable to overwrite high score file: " + e.getMessage();
			JOptionPane.showMessageDialog(this.frame, msg, "Save Error",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	/**
	 * adds the score the the high score table by making sure its ordered, then going through
	 * and iterating until it finds a number smaller then it and inserts itself right before it
	 * then it updates the list.
	 * @param score
	 */
	public void addScore(int score) {
		//internalLoader();
		try {
			checkOrder();
			System.out.println(this.highScoreArrayList.size());
			for (int k = 0; k < this.highScoreArrayList.size(); k++) {
				if (score > this.highScoreArrayList.get(k)) {
					this.highScoreArrayList.add(k, score);
					overwrite();
					return;
				}
			}
			this.highScoreArrayList.add(score);
			overwrite();
		} catch (FileNotFoundException e) {
			handleFileNotFoundException(e);
		}
	}

	/**
	 * handles file not found exceptions by creating a new high score table and trying again
	 * @param e
	 */
	private void handleFileNotFoundException(FileNotFoundException e) {
		String msg = "No high score file, so I created one for you! Please refresh Eclipse: "
				+ e.getMessage();
		System.out.println(msg);
		JOptionPane
				.showMessageDialog(this.frame, msg, "Save Error", JOptionPane.ERROR_MESSAGE);
		this.highScoreFile = new File("src/High Score.txt");
		try {
			this.highScoreFile.createNewFile();
			internalLoader();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

}