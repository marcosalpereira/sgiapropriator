package br.com.marcosoft.sgi;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

import br.com.marcosoft.sgi.util.MoveMouseListener;

public class Main extends JPanel {
  @Override
public void paintComponent(Graphics g) {
    g.setColor(Color.black);
    g.fillRect(0, 0, getWidth(), getHeight());
  }

  public static void main(String[] args) {
    final JFrame frame = new JFrame();
    frame.setPreferredSize(new Dimension(300, 280));

    final Main ch = new Main();
    frame.getContentPane().add(ch);
    frame.setUndecorated(true);

    final MoveMouseListener mml = new MoveMouseListener(ch);
    ch.addMouseListener(mml);
    ch.addMouseMotionListener(mml);

    frame.pack();
    frame.setVisible(true);
  }
}

