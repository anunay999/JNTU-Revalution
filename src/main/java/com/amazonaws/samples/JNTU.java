package com.amazonaws.samples;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JCheckBox;
import java.awt.Font;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.awt.event.ActionEvent;

public class JNTU {

	private JFrame frame;
	private JTextField htno;
	private JTextField subject;
	private JTextField file;
	private File _file;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JNTU window = new JNTU();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public JNTU() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 568, 494);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblCollege = new JLabel("College  : ");
		lblCollege.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblCollege.setBounds(52, 95, 88, 22);
		frame.getContentPane().add(lblCollege);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"  ","Keshav Memorial Institue of Technology(KMIT)", "Neil Goge Institute of Technology(NGIT)", "Mahatma Gandi Institute of Technology(MGIT)", "Chaitanya Bharathi Instittue of Technology(CBIT)"}));
		comboBox.setBounds(207, 89, 240, 27);
		frame.getContentPane().add(comboBox);
		
		JLabel lblRevaluationForm = new JLabel("Revaluation Form");
		lblRevaluationForm.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblRevaluationForm.setBounds(167, 23, 179, 25);
		frame.getContentPane().add(lblRevaluationForm);
		
		JLabel lblHallTicketNo = new JLabel("Hall Ticket No.  :  ");
		lblHallTicketNo.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblHallTicketNo.setBounds(52, 159, 131, 19);
		frame.getContentPane().add(lblHallTicketNo);
		
		htno = new JTextField();
		htno.setFont(new Font("Tahoma", Font.PLAIN, 13));
		htno.setBounds(207, 153, 240, 25);
		frame.getContentPane().add(htno);
		htno.setColumns(10);
		
		JLabel lblSubject = new JLabel("Subject  :  ");
		lblSubject.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblSubject.setBounds(52, 222, 77, 19);
		frame.getContentPane().add(lblSubject);
		
		subject = new JTextField();
		subject.setFont(new Font("Tahoma", Font.PLAIN, 13));
		subject.setColumns(10);
		subject.setBounds(207, 220, 240, 25);
		frame.getContentPane().add(subject);
		
		JLabel lblFileName = new JLabel("File : ");
		lblFileName.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblFileName.setBounds(52, 283, 92, 19);
		frame.getContentPane().add(lblFileName);
		
		file = new JTextField();
		file.setFont(new Font("Tahoma", Font.PLAIN, 13));
		file.setColumns(10);
		file.setBounds(207, 283, 131, 25);
		frame.getContentPane().add(file);
		
		
		JButton btnUpload = new JButton("Upload");
		btnUpload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc=new JFileChooser();  
				fc.showOpenDialog(null);
				_file=fc.getSelectedFile();
				String file_name=_file.getName();
				file.setText(file_name);
				
			}
		});

		JButton btnSubmit = new JButton("Submit");
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String clg=(String) comboBox.getItemAt(comboBox.getSelectedIndex());
				String str = clg;
				String answer = str.substring(str.indexOf("(")+1,str.indexOf(")"));
				JNTUS3 s3=new JNTUS3(htno.getText(),answer,subject.getText());
				SimpleDBEval sdb=new SimpleDBEval(htno.getText(),answer,subject.getText(),LocalDateTime.now());
				String oid=sdb.findWeekExpire();
				if(oid!=null)
				{
					String [] temp=oid.split("-");
					String bName="com.jntu.evaluation."+temp[1].toLowerCase();
					s3.deleteObject(bName, oid);
				}
				s3.addObject(_file);
				sdb.addDetails();
				JOptionPane.showMessageDialog(frame,"Request submitted");
				frame.dispose();
			}
		});
		btnSubmit.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnSubmit.setBounds(414, 376, 89, 27);
		frame.getContentPane().add(btnSubmit);
		
		btnUpload.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnUpload.setBounds(386, 285, 89, 23);
		frame.getContentPane().add(btnUpload);
	}
}
