import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.border.EtchedBorder;

import oracle.sdoapi.OraSpatialManager;
import oracle.sdoapi.geom.*;
import oracle.sdoapi.adapter.*;
import oracle.sdoapi.*;
import oracle.sql.STRUCT;


public class hw2 extends JFrame implements MouseListener,MouseMotionListener{	
    Image img;
	ImagePanel p_map;
	JPanel pc,pr;
	JTextArea outputsql;
	JTextField mouseXY;
	JLabel text1,text2;
	JCheckBox AS,Building,Students;
	ButtonGroup geoquery;
	JRadioButton we,pq,rq,ss,eq;
	JButton submit;	
	static int pointquery_x,pointquery_y; 
	ResultSet mainResultSet = null;
	static Statement mainStatement = null;
    ArrayList<MousePoint> mr = new ArrayList<MousePoint>();
	static int colorchoice=0;
	static boolean isRightClick=false;
    static int choice;
    static boolean isAS=false, isBuilding=false, isStudent=false;
	
	/*choice=0  Repaint()
	 *choice=1  Whole Region
	 *choice=2  Point Query 
	 *choice=3  Range Query
	 *choice=4  Surrounding Students
	 *choice=5  Emergency Query
	 *choice=6  Show Selected Point in Point Query 
	 *choice=7  Show Selected Aera
	 * */

	//----------Main Function------------//
	public static void main(String args[]){
		hw2 wk_frame = new hw2();
	}
	
	//----------------Class Function-----------------//
	public hw2() {
		setResizable(false);
        init();
        this.setTitle("Kang Wang   USC-ID:1093294783");
        this.setSize(1000,700);
        this.setVisible(true);
        addWindowListener(new WindowAdapter(){
        	 public void windowClosing(WindowEvent event)
        	    {
        	       System.exit(0);
        	    }  
        });
        //enableEvents(AWTEvent.WINDOW_EVENT_MASK); 

        
    }

	
	 //-------------------------GUI-----------------------------//
	 public void init() {
		 
		 EtchedBorder e = new EtchedBorder();
		 //img=Toolkit.getDefaultToolkit().getImage("img/map.jpg");
		 p_map = new ImagePanel("img/map.jpg", this);
		 //p_map.setSize(840,590);
		 pc = new JPanel(); 
		 pc.setSize(180, 580);
		 pr = new JPanel();
		 pr.setSize(1000, 120);
		 AS = new JCheckBox("AS",true);
		 AS.setFont(new Font("Times New Roman", Font.BOLD, 13));
		 AS.setHorizontalAlignment(SwingConstants.LEFT);
		 Building = new JCheckBox("Building",true);
		 Building.setFont(new Font("Times New Roman", Font.BOLD, 13));
		 Building.setHorizontalAlignment(SwingConstants.LEFT);
		 Students = new JCheckBox("Students",true);
		 Students.setFont(new Font("Times New Roman", Font.BOLD, 13));
		 we = new JRadioButton("Whole region",true);
		 we.setFont(new Font("Times New Roman", Font.BOLD, 13));
		 pq = new JRadioButton("Point Query");
		 pq.setFont(new Font("Times New Roman", Font.BOLD, 13));
		 rq = new JRadioButton("Range Query");
		 rq.setFont(new Font("Times New Roman", Font.BOLD, 13));
		 ss = new JRadioButton("Surrounding Student");
		 ss.setFont(new Font("Times New Roman", Font.BOLD, 13));
		 eq = new JRadioButton("Emergency Query");
		 eq.setFont(new Font("Times New Roman", Font.BOLD, 13));
		 geoquery = new ButtonGroup();		 
		 geoquery.add(we);geoquery.add(pq);geoquery.add(rq);geoquery.add(ss);geoquery.add(eq);
		 text1 = new JLabel("Active Feature Type");
		 text1.setHorizontalAlignment(SwingConstants.LEFT);
		 text1.setFont(new Font("Times New Roman", Font.BOLD, 15));
		 text2 = new JLabel("Query");
		 text2.setFont(new Font("Times New Roman", Font.BOLD, 13));
		 mouseXY = new JTextField();
		 mouseXY.setFont(new Font("Times New Roman", Font.BOLD, 15));
	     //mouseXY.setText("mouse point");
		 outputsql = new JTextArea("Your submitted query should be dispalyed here");
		 outputsql.setEditable(false);
		 //outputsql.setLineWrap(true);
		 JScrollPane scrollpane= new JScrollPane(outputsql, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		 scrollpane.setPreferredSize(new Dimension(980,70));
		 submit = new JButton("Submit   Query");
		 submit.setFont(new Font("Times New Roman", Font.BOLD, 15));
		 submit.setHorizontalAlignment(SwingConstants.CENTER);
		 /*BorderLayout b = new BorderLayout();
		 this.setLayout(b);*/
		 GridLayout gl_pc = new GridLayout(12,1);
		 gl_pc.setHgap(5);
		 pc.setLayout(gl_pc);
		 
		 getContentPane().add(p_map,BorderLayout.CENTER);
		 getContentPane().add(pc,BorderLayout.EAST);
		 getContentPane().add(pr,BorderLayout.SOUTH);
		 
		 pc.add(text1);
		 pc.add(AS);
		 pc.add(Building);
		 pc.add(Students);
		 pc.add(text2);
		 pc.add(we);
		 pc.add(pq);
		 pc.add(rq);
		 pc.add(ss);
		 pc.add(eq);
		 pc.add(mouseXY);
		 pc.add(submit);
		 pr.add(scrollpane);
		 
		 
		 //----------------------Listeners---------------------//
		 p_map.addMouseListener(this);
		 p_map.addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseMoved(MouseEvent e) {
					mouseXY.setText("          "+e.getX() + "," + e.getY());
				}
			});
		 
		 we.addActionListener(new ActionListener(){
			 public void actionPerformed(ActionEvent actionEvent) {
				 p_map.setChoice(0);
				 p_map.repaint();

			 }
		 });
		 
		 pq.addActionListener(new ActionListener(){
			 public void actionPerformed(ActionEvent actionEvent) {
				 p_map.setChoice(0);
				 p_map.repaint();

			 }
		 });
		 
		 rq.addActionListener(new ActionListener(){
			 public void actionPerformed(ActionEvent actionEvent) {
				 p_map.setChoice(0);
				 p_map.repaint();

			 }
		 });
		 
		 ss.addActionListener(new ActionListener(){
			 public void actionPerformed(ActionEvent actionEvent) {
				 p_map.setChoice(0);
				 p_map.repaint();

			 }
		 });
		 
		 eq.addActionListener(new ActionListener(){
			 public void actionPerformed(ActionEvent actionEvent) {
				 p_map.setChoice(0);
				 p_map.repaint();

			 }
		 });

		 
		 submit.addActionListener(new ActionListener() {
			 //String tablename;
			 //StringBuilder temp =new StringBuilder();

			 public void actionPerformed(ActionEvent actionEvent) {
				 
				 if(AS.isSelected()){
					 isAS=true;
				 }else{
					 isAS=false;
				 }
				 
				 if(Building.isSelected()){
					 isBuilding=true;
				 }else{
					 isBuilding=false;
				 }
				 
				 if(Students.isSelected()){
					 isStudent=true;
				 }else{
					 isStudent=false;
				 }
				 
	//whole region			 
				 if(we.isSelected()){
					choice=1;
					try {
						p_map.setConnection(ConnectToMyDB(),mainStatement);
					} catch (IOException | SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					p_map.setChoice(choice);
					p_map.setCheckBox(isAS, isBuilding, isStudent);
					outputsql.append(p_map.sql1);
					p_map.repaint();
		
					//outputsql.setText("choice="+choice);
					
				 }

				 if(pq.isSelected()){
						choice=2;
						try {
							p_map.setConnection(ConnectToMyDB(),mainStatement);
						} catch (IOException | SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                        p_map.setPointQueryXY(pointquery_x, pointquery_y);
						p_map.setChoice(choice);
						p_map.setCheckBox(isAS, isBuilding, isStudent);
						outputsql.append(p_map.sql2);
						p_map.repaint();
						//outputsql.setText("choice="+choice);
					 }else{
						 p_map.repaint();
					 }
				 
				 if(rq.isSelected()){
						choice=3;
						try {
							p_map.setConnection(ConnectToMyDB(),mainStatement);
						} catch (IOException | SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						p_map.setChoice(choice);
						//p_map.setRange(mr);
						p_map.setCheckBox(isAS, isBuilding, isStudent);
						p_map.setRange(mr);
						outputsql.append(p_map.sql3);
						//mr.clear();
						//outputsql.setText("choice="+choice);
					 }
				 
				 if(ss.isSelected()){
						choice=4;
						try {
							p_map.setConnection(ConnectToMyDB(),mainStatement);
						} catch (IOException | SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						p_map.setChoice(choice);
						p_map.setCheckBox(isAS, isBuilding, isStudent);
						outputsql.append(p_map.sql4);
						p_map.repaint();
						//outputsql.setText("choice="+choice);
					 }
				 
				 if(eq.isSelected()){
						choice=5;
						try {
							p_map.setConnection(ConnectToMyDB(),mainStatement);
						} catch (IOException | SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						p_map.setChoice(choice);
						p_map.setCheckBox(isAS, isBuilding, isStudent);
						outputsql.append(p_map.sql5);
						p_map.repaint();
						//outputsql.setText("choice="+choice);
					 }

			 }
		 });
		 
	 }
	
  
	          
      
  //-----------------------Connecting to DB----------------------------//
  public static Connection ConnectToMyDB() throws IOException, SQLException
	{
	  Connection mainConnection;
	  //Statement mainStatement = null;
		try
		{
			// loading Oracle Driver
  		    //System.out.print("Looking for Oracle's jdbc-odbc driver ... ");
	    	DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
	    	//System.out.println(", Loaded.");

			String URL = "jdbc:oracle:thin:@localhost:1521:kangwang";
	    	String userName = "wk_test";
	    	String password = "wk523";

	    	//System.out.print("Connecting to DB...");
	    	mainConnection = DriverManager.getConnection(URL, userName, password);
	    	//System.out.println(", Connected!");

  		mainStatement = mainConnection.createStatement();
  		
  		return mainConnection;
  		
 		}
 		catch (Exception e)
 		{
   		System.out.println( "Error while connecting to DB: "+ e.toString() );
   		e.printStackTrace();
   		System.exit(-1);
   		return null;
 		}
		
	}
  

@Override
public void mouseDragged(MouseEvent e) {
	// TODO Auto-generated method stub
	
}


@Override
public void mouseMoved(MouseEvent e) {
	// TODO Auto-generated method stub
	//mouseXY.setText(e.getX()+","+e.getY());
	System.out.println(e.getX()+","+e.getY());
}


@Override
public void mouseClicked(MouseEvent e) {
	// TODO Auto-generated method stub
	//pointquery_x=0;
	//pointquery_y=0;
  if(pq.isSelected()){
	choice=6;
	//MousePoint m_pq = new MousePoint();
	pointquery_x=e.getX();
	pointquery_y=e.getY();
	p_map.setPointQueryXY(pointquery_x, pointquery_y);
	p_map.setChoice(choice);
	p_map.repaint();
  }
  
  if(rq.isSelected()){
	  //p_map.setChoice(0);
	if(e.getButton()==MouseEvent.BUTTON1){
		MousePoint mouse = new MousePoint();
		if(isRightClick){
		mr.clear();
		isRightClick=false;

		}
		mouse.x=e.getX();
		mouse.y=e.getY();
		p_map.getPointXY(mouse.x, mouse.y);
		p_map.setChoice(9);
		mr.add(mouse);
	  //outputsql.setText("Range Query-Left");
	}
	if(e.getButton()==MouseEvent.BUTTON3){
		//String pointstr = "";
		isRightClick=true;
		p_map.setChoice(7);
		p_map.setRange(mr);
		//mr.clear();
		p_map.repaint();

	}
	
  }
  
  if(ss.isSelected()){
	  MousePoint mouse = new MousePoint();
	  mouse.x=e.getX();
	  mouse.y=e.getY();
	  p_map.setSSQPoint(mouse.x, mouse.y);
  }
  
  if(eq.isSelected()){
		try {
			p_map.setConnection(ConnectToMyDB(),mainStatement);
		} catch (IOException | SQLException m) {
			// TODO Auto-generated catch block
			m.printStackTrace();
		}
	  MousePoint mouse = new MousePoint();
	  mouse.x=e.getX();
	  mouse.y=e.getY();
	  p_map.setSelectedPoint(mouse.x, mouse.y);
	  p_map.setChoice(8);
	  p_map.repaint();
	  
  }
	
}


@Override
public void mouseEntered(MouseEvent e) {
	// TODO Auto-generated method stub
	
}


@Override
public void mouseExited(MouseEvent e) {
	// TODO Auto-generated method stub
	
}


@Override
public void mousePressed(MouseEvent e) {
	// TODO Auto-generated method stub
	
}


@Override
public void mouseReleased(MouseEvent e) {
	// TODO Auto-generated method stub
	
}


public void fillRect(int x, int y, int i, int j) {
	// TODO Auto-generated method stub
	
}


public void drawOval(int i, int j, int k, int l) {
	// TODO Auto-generated method stub
	
}

  
}



//------------------ImagePanel-----------------------//
class ImagePanel extends JPanel {

	  Image img;
	  int choice_;
	  int pq_x,pq_y;
	  int ssp_x,ssp_y;
	  int eq_x,eq_y;
	  int p_x,p_y;
	  int pointcount=0;
	  Boolean isB,isAS,isStu;
	  Connection conn;
	  Statement stat;
	  ArrayList<MousePoint> polygonarray = new ArrayList<MousePoint>();
	  ArrayList<MousePoint> pointXY = new ArrayList<MousePoint>();
	  int colorchoice=3;
	  HashMap<String, Color> colorMap = new HashMap<String, Color>();
	  Color[] colors = {Color.CYAN, Color.MAGENTA, Color.YELLOW, Color.PINK, Color.ORANGE};
	  String sql1;
	  String sql2;
	  String sql3;
	  String sql4;
	  String sql5;
	  String sql6;
	  StringBuilder sql = new StringBuilder();
	  hw2 HW2;
	  ImagePanel(String img_name, hw2 h)
	  {

	  img = Toolkit.getDefaultToolkit().getImage(img_name);

      HW2=h;
	  //setSize(img.getWidth(this), img.getHeight(this));
	  setSize(820,580);
	  }

	  public void setConnection(Connection c, Statement s){
		  conn=c;
		  stat=s;
	  }
	  
	  public void setChoice(int c)
	  {
		  choice_ = c;
	  }
	  
	  
	  public void setCheckBox(Boolean a, Boolean b, Boolean c){
		  isAS=a;
		  isB=b;
		  isStu=c;
	  }
	  
	  public void setPointQueryXY(int x, int y){
		  pq_x=x;
		  pq_y=y;
	  }
	  
	  public void setRange(ArrayList<MousePoint> m){
		  polygonarray = m;
	  }
	  
	  public void setSSQPoint(int x, int y){
		  ssp_x=x;
		  ssp_y=y;
	  }
	  
	  public void setSelectedPoint(int x, int y){
		  eq_x=x;
		  eq_y=y;
	  }
	  
	  public void getPointXY(int x, int y){
		  p_x=x;
		  p_y=y;
	  }
	  	  
	          @Override
	  		  public void paint(Graphics g) {
	        	  
	                          ((Graphics2D)g).drawImage(img, 0, 0, this);
	                          
	                          
	                          switch(choice_){
	                          
	                          //-----------------Whole Region-------------------//
	                          case 0:
	                        	  ((Graphics2D)g).drawImage(img, 0, 0, this);
	                        	  repaint();
	                        	  break;
	                          case 1:
	                        	  if(isAS){
	                        		    STRUCT point;		//Structure to handle Geometry Objects
	                        			Geometry geom;     	//Structure to handle Geometry Objects
	                        			int raduis;
	                        			
                                        sql.append("\nSELECT * FROM ASYS\n");
	                        			try
	                        			{
	                        	                                // shows result of the query

	                        		        ResultSet mainResultSet=stat.executeQuery("SELECT * FROM ASYS");

	                        		  		GeometryAdapter sdoAdapter = OraSpatialManager.getGeometryAdapter("SDO", "9",STRUCT.class, null, null, conn);

	                        	 	        while( mainResultSet.next() )
	                        	    	    {
	                        	 	        	raduis = (int)mainResultSet.getInt(2);
	                        	 	        	point = (STRUCT)mainResultSet.getObject(3);
	                        					geom = sdoAdapter.importGeometry( point );
	                        	      			if ( (geom instanceof oracle.sdoapi.geom.Point) )
	                        	      			{
	                        						oracle.sdoapi.geom.Point point0 = (oracle.sdoapi.geom.Point) geom;
	                        						int X = (int)point0.getX();
	                        						int Y = (int)point0.getY();
	                        						g.setColor(Color.RED);
	                        						g.drawOval(X-raduis, Y-raduis, 2*raduis, 2*raduis);
	                        					    g.fillRect(X-7, Y-7, 15, 15);
	                        						
	                        					}

	                        	       	    }
	                        	        }
	                        			catch( Exception e )
	                        		    { System.out.println(" Error : " + e.toString() ); }

	                        			System.out.println();
	                        		  //g.fillOval(x, y, width, height); draw circle
	                        	  }
	                        	  if(isB){
	                        		  STRUCT polygon;		//Structure to handle Geometry Objects
	                        		  Geometry geom;     	//Structure to handle Geometry Objects
                                      sql.append("\nSELECT * FROM BUILDING\n");
	                        			try
	                        			{
	                        	                                // shows result of the query
	              
	                        		        ResultSet mainResultSet=stat.executeQuery("SELECT * FROM BUILDING");
	                        		      
	                        		  		GeometryAdapter sdoAdapter = OraSpatialManager.getGeometryAdapter("SDO", "9",STRUCT.class, null, null, conn);
	                        		  		while(mainResultSet.next()){
	                        		  		polygon=(STRUCT)mainResultSet.getObject(4);
	                        		  		geom = sdoAdapter.importGeometry( polygon );
	                        		  		if ( (geom instanceof oracle.sdoapi.geom.Polygon) ){
	                        		  			oracle.sdoapi.geom.Polygon polygon0 = (oracle.sdoapi.geom.Polygon) geom;
	                        		  			for (Enumeration e = polygon0.getRings(); e.hasMoreElements();) 
	                        		  			{
	                        		  				
	                        		  					LineString lineString = (LineString)e.nextElement();
	                        		  					
	                        		  					
	                        		  				CoordPoint[] coordArray = lineString.getPointArray();
	                        		  				int[] contour_x = new int[coordArray.length];
	                        		  				int[] contour_y = new int[coordArray.length];
	                 
	                        		  				for (int i = 0; i < coordArray.length; i++)  //extract each vertex of the polynon
	                        		  				{
	                        		  						g.setColor(Color.yellow);
	                        		  						contour_x[i] = (int) coordArray[i].getX();
	                        		  						contour_y[i] = (int) coordArray[i].getY();
	                        		  					if(i>0){
	                        		  						g.drawLine(contour_x[i], contour_y[i], contour_x[i-1], contour_y[i-1]);
	                        		  					}
	                        		  				}
	                        		  			}
	                        		  		}
	                        		  	}
	                        			}catch( Exception e )
	                        		    { System.out.println(" Error : " + e.toString() ); }
	                        	
	                        	  }
	                        	  if(isStu){
	                        		    STRUCT point;		//Structure to handle Geometry Objects
	                        			Geometry geom;     	//Structure to handle Geometry Objects
                                        sql.append("\nSELECT * FROM STUDENT\n");
	                        			try
	                        			{
	                        	                                // shows result of the query

	                        		        ResultSet mainResultSet=stat.executeQuery("SELECT * FROM STUDENT");

	                        		  		GeometryAdapter sdoAdapter = OraSpatialManager.getGeometryAdapter("SDO", "9",STRUCT.class, null, null, conn);

	                        	 	        while( mainResultSet.next() )
	                        	    	    {
	                        		    	    point = (STRUCT)mainResultSet.getObject(2);
	                        					geom = sdoAdapter.importGeometry( point );
	                        	      			if ( (geom instanceof oracle.sdoapi.geom.Point) )
	                        	      			{
	                        						oracle.sdoapi.geom.Point point0 = (oracle.sdoapi.geom.Point) geom;
	                        						int X = (int)point0.getX();
	                        						int Y = (int)point0.getY();
	                        						g.setColor(Color.GREEN);
	                        						g.fillRect(X-5, Y-5, 10, 10);
	                        					}

	                        	       	    }
	                        	        }
	                        			catch( Exception e )
	                        		    { System.out.println(" Error : " + e.toString() ); }

	                        			System.out.println();
	                     
	                        	  }
	                        	  sql1=sql.toString();
	                        	  repaint();                        	    
	                        	  break;
	                        	  
	                          //-----------------Point Query-------------------//
	                          case 2:	                        	  
	                        	  g.setColor(Color.RED);
	                        	  g.fillRect(pq_x-2, pq_y-2, 5, 5);
	                    		  g.drawOval(pq_x-50, pq_y-50, 100, 100);
	                        	  if(isAS){
	                        		    STRUCT point;		//Structure to handle Geometry Objects
	                        			Geometry geom;     	//Structure to handle Geometry Objects
	                        			int raduis;
                                  sql.append("\nSELECT * FROM ASYS a "
	                        		        +"WHERE SDO_WITHIN_DISTANCE(a.ASCENTER, SDO_GEOMETRY(2001, NULL, SDO_POINT_TYPE("+ pq_x+ ", " + pq_y + ", NULL), NULL, NULL), 'distance='||TO_CHAR(a.ASRADIUS+ 50)) = 'TRUE' "
	                        		        + "ORDER BY SDO_GEOM.SDO_DISTANCE(a.ASZONE, SDO_GEOMETRY(2001, NULL, SDO_POINT_TYPE("+ pq_x + ", " + pq_y + ", NULL), NULL, NULL), 1)\n");
	                        			try
	                        			{

	                        		        ResultSet mainResultSet=stat.executeQuery("SELECT * FROM ASYS a "
	                        		        +"WHERE SDO_WITHIN_DISTANCE(a.ASCENTER, SDO_GEOMETRY(2001, NULL, SDO_POINT_TYPE("+ pq_x+ ", " + pq_y + ", NULL), NULL, NULL), 'distance='||TO_CHAR(a.ASRADIUS+ 50)) = 'TRUE' "
	                        		        + "ORDER BY SDO_GEOM.SDO_DISTANCE(a.ASZONE, SDO_GEOMETRY(2001, NULL, SDO_POINT_TYPE("+ pq_x + ", " + pq_y + ", NULL), NULL, NULL), 1)");
	                        		       
	                        		  		GeometryAdapter sdoAdapter = OraSpatialManager.getGeometryAdapter("SDO", "9",STRUCT.class, null, null, conn);

	                        		  		int i=0;
	                        	 	        while( mainResultSet.next() )
	                        	    	    {
	                        	 	        	i++;
	                        	 	        	raduis = (int)mainResultSet.getInt(2);
	                        	 	        	point = (STRUCT)mainResultSet.getObject(3);
	                        					geom = sdoAdapter.importGeometry( point );
	                        	      			if ( (geom instanceof oracle.sdoapi.geom.Point) )
	                        	      			{
	                        						oracle.sdoapi.geom.Point point0 = (oracle.sdoapi.geom.Point) geom;
	                        						int X = (int)point0.getX();
	                        						int Y = (int)point0.getY();
	                        						if(i==1){
	                        							g.setColor(Color.YELLOW);
	                        							g.drawOval(X-raduis, Y-raduis, 2*raduis, 2*raduis);
		                        					    g.fillRect(X-7, Y-7, 15, 15);
	                        						}else{
	                        						g.setColor(Color.GREEN);
	                        						g.drawOval(X-raduis, Y-raduis, 2*raduis, 2*raduis);
	                        					    g.fillRect(X-7, Y-7, 15, 15);
	                        						}
	                        						
	                        					}

	                        	       	    }
	                        	 	        
	                        	 	        repaint();
	                        	        }
	                        			catch( Exception e )
	                        		    { System.out.println(" Error : " + e.toString() ); }
	                        	  }
	                        	  if(isStu){
	                        		    STRUCT point;		//Structure to handle Geometry Objects
	                        			Geometry geom;     	//Structure to handle Geometry Objects
                                        sql.append("\nSELECT * FROM STUDENT a "
    	                        		        + "WHERE SDO_WITHIN_DISTANCE(a.STULOC, SDO_GEOMETRY(2001, NULL, SDO_POINT_TYPE("+ pq_x+ ", " + pq_y + ", NULL), NULL, NULL), 'distance=50') = 'TRUE' "
    	                        		        + "ORDER BY SDO_GEOM.SDO_DISTANCE(a.STULOC, SDO_GEOMETRY(2001, NULL, SDO_POINT_TYPE("+ pq_x + ", " + pq_y + ", NULL), NULL, NULL), 1)\n");
	                        			try
	                        			{

	                        		        ResultSet mainResultSet=stat.executeQuery("SELECT * FROM STUDENT a "
	                        		        + "WHERE SDO_WITHIN_DISTANCE(a.STULOC, SDO_GEOMETRY(2001, NULL, SDO_POINT_TYPE("+ pq_x+ ", " + pq_y + ", NULL), NULL, NULL), 'distance=50') = 'TRUE' "
	                        		        + "ORDER BY SDO_GEOM.SDO_DISTANCE(a.STULOC, SDO_GEOMETRY(2001, NULL, SDO_POINT_TYPE("+ pq_x + ", " + pq_y + ", NULL), NULL, NULL), 1)");
	                        		       
	                        		  		GeometryAdapter sdoAdapter = OraSpatialManager.getGeometryAdapter("SDO", "9",STRUCT.class, null, null, conn);

	                        		  		int i=0;
	                        	 	        while( mainResultSet.next() )
	                        	    	    {
	                        	 	        	i++;
	                        	 	    
	                        	 	        	point = (STRUCT)mainResultSet.getObject(2);
	                        					geom = sdoAdapter.importGeometry( point );
	                        	      			if ( (geom instanceof oracle.sdoapi.geom.Point) )
	                        	      			{
	                        						oracle.sdoapi.geom.Point point0 = (oracle.sdoapi.geom.Point) geom;
	                        						int X = (int)point0.getX();
	                        						int Y = (int)point0.getY();
	                        						if(i==1){
	                        							g.setColor(Color.YELLOW);	                        							
		                        					    g.fillRect(X-2, Y-2, 5, 5);
	                        						}else{
	                        						g.setColor(Color.GREEN);                 
	                        					    g.fillRect(X-2, Y-2, 5, 5);
	                        						}
	                        						
	                        					}

	                        	       	    }
	                        	 	        
	                        	 	        repaint();
	                        	        }
	                        			catch( Exception e )
	                        		    { System.out.println(" Error : " + e.toString() ); }
	                        	  }
	                        	  if(isB){
	                        		  STRUCT polygon;		//Structure to handle Geometry Objects
	                        		  Geometry geom;     	//Structure to handle Geometry Objects
                                      sql.append("\nSELECT * FROM BUILDING b "
	                        		        +"WHERE SDO_ANYINTERACT(b.BZONE, SDO_GEOMETRY(2003, NULL, NULL, SDO_ELEM_INFO_ARRAY(1, 1003, 4), SDO_ORDINATE_ARRAY("+pq_x+"," +(pq_y-50)+"," +(pq_x+50)+"," +pq_y+"," +pq_x+","+ (pq_y+50)+")))= 'TRUE'"
                                            +"ORDER BY SDO_GEOM.SDO_DISTANCE(b.BZONE, SDO_GEOMETRY(2001, NULL, SDO_POINT_TYPE("+ pq_x + ", " + pq_y + ", NULL), NULL, NULL), 1)\n");
	                        			try
	                        			{
	                        	                                // shows result of the query
	              
	                        		        ResultSet mainResultSet=stat.executeQuery("SELECT * FROM BUILDING b "
	                        		        +"WHERE SDO_ANYINTERACT(b.BZONE, SDO_GEOMETRY(2003, NULL, NULL, SDO_ELEM_INFO_ARRAY(1, 1003, 4), SDO_ORDINATE_ARRAY("+pq_x+"," +(pq_y-50)+"," +(pq_x+50)+"," +pq_y+"," +pq_x+","+ (pq_y+50)+")))= 'TRUE'"
                                            +"ORDER BY SDO_GEOM.SDO_DISTANCE(b.BZONE, SDO_GEOMETRY(2001, NULL, SDO_POINT_TYPE("+ pq_x + ", " + pq_y + ", NULL), NULL, NULL), 1)");
	                        		      
	                        		  		GeometryAdapter sdoAdapter = OraSpatialManager.getGeometryAdapter("SDO", "9",STRUCT.class, null, null, conn);
	                        		  		int j=0;
	                        		  		while(mainResultSet.next()){
	                        		  			j++;
	                        		  		polygon=(STRUCT)mainResultSet.getObject(4);
	                        		  		geom = sdoAdapter.importGeometry( polygon );
	                        		  		if ( (geom instanceof oracle.sdoapi.geom.Polygon) ){
	                        		  			oracle.sdoapi.geom.Polygon polygon0 = (oracle.sdoapi.geom.Polygon) geom;
	                        		  			for (Enumeration e = polygon0.getRings(); e.hasMoreElements();) 
	                        		  			{
	                        		  				
	                        		  					LineString lineString = (LineString)e.nextElement();
	                        		  					
	                        		  					
	                        		  				CoordPoint[] coordArray = lineString.getPointArray();
	                        		  				int[] contour_x = new int[coordArray.length];
	                        		  				int[] contour_y = new int[coordArray.length];
	                 
	                        		  			if(j==1){
	                        		  				for (int i = 0; i < coordArray.length; i++)  //extract each vertex of the polynon
	                        		  				{
	                        		  						g.setColor(Color.YELLOW);
	                        		  						contour_x[i] = (int) coordArray[i].getX();
	                        		  						contour_y[i]= (int) coordArray[i].getY();
	                        		  					if(i>0){
	                        		  						g.drawLine(contour_x[i], contour_y[i], contour_x[i-1], contour_y[i-1]);
	                        		  					}
	                        		  				}
	                        		  			}else{
	                        		  				for (int i = 0; i < coordArray.length; i++)  //extract each vertex of the polynon
	                        		  				{
	                        		  						g.setColor(Color.GREEN);
	                        		  						contour_x[i] = (int) coordArray[i].getX();
	                        		  						contour_y[i]= (int) coordArray[i].getY();
	                        		  					if(i>0){
	                        		  						g.drawLine(contour_x[i], contour_y[i], contour_x[i-1], contour_y[i-1]);
	                        		  					}
	                        		  			    }
	                        		  			}
	                        		  		}
	                        		  		}
	                        		  	}
	                        			}catch( Exception e )
	                        		    { System.out.println(" Error : " + e.toString() ); }
	                        	  }
	                        	  sql2=sql.toString();
	                        	  repaint();
	                        	  break;
	                          case 3:
	                        	  String pointstr="";
	                        	  for(int i=0;i<polygonarray.size();i++){
	                        		  g.setColor(Color.RED);
	                        		 if(i>0){	                        		
	                        		  g.drawLine(polygonarray.get(i).x, polygonarray.get(i).y, polygonarray.get(i-1).x, polygonarray.get(i-1).y);
	                        		 }
	                        		 if(i==polygonarray.size()-1){
	                        		  g.drawLine(polygonarray.get(i).x, polygonarray.get(i).y, polygonarray.get(0).x, polygonarray.get(0).y); 
	                        		 }
	                        	  }
	                              for(int i=0;i<polygonarray.size();i++){
	                              	pointstr += polygonarray.get(i).x;
	                              	pointstr += ",";
	                              	pointstr += polygonarray.get(i).y;
	                              	if (i != polygonarray.size() - 1)
	                              		pointstr += ",";
	                              	else
	                              		pointstr += ","+polygonarray.get(0).x+","+polygonarray.get(0).y;
	                             
	                              }
	                              //System.out.println(pointstr);
	                        	   if(isAS){
	                        		    STRUCT point;		//Structure to handle Geometry Objects
	                        			Geometry geom;     	//Structure to handle Geometry Objects
	                        			int raduis;
                                        sql.append("\nSELECT * FROM ASYS B WHERE sdo_relate(B.ASZONE,"                          
                                                +"SDO_GEOMETRY("                      
    	                        				+ "2003,"                            
                                                + "NULL,"                     
    	                        				+ "NULL,"                     
                                                + "SDO_ELEM_INFO_ARRAY(1,1003,1),"                    
    	                        				+ "SDO_ORDINATE_ARRAY("                     
                                                + pointstr+")), "                    
    	                        				+ "\'mask=anyinteract\') = \'TRUE\'\n");
	                        			try
	                        			{

	                        		        /*ResultSet mainResultSet=stat.executeQuery("SELECT * FROM ASYS a"
                                            +"WHERE SDO_ANYINTERACT(a.ASZONE, SDO_GEOMETRY(2003, NULL, NULL, SDO_ELEM_INFO_ARRAY(1,1003,1), SDO_ORDINATE_ARRAY("+pointstr+"))) = 'TRUE'");*/
	                        				ResultSet mainResultSet=stat.executeQuery("SELECT * FROM ASYS B WHERE sdo_relate(B.ASZONE,"                          
                                            +"SDO_GEOMETRY("                      
	                        				+ "2003,"                            
                                            + "NULL,"                     
	                        				+ "NULL,"                     
                                            + "SDO_ELEM_INFO_ARRAY(1,1003,1),"                    
	                        				+ "SDO_ORDINATE_ARRAY("                     
                                            + pointstr+")), "                    
	                        				+ "\'mask=anyinteract\') = \'TRUE\'");
	                        		  		GeometryAdapter sdoAdapter = OraSpatialManager.getGeometryAdapter("SDO", "9",STRUCT.class, null, null, conn);

	                        	 	        while( mainResultSet.next() )
	                        	    	    {
	                        	 	        	raduis = (int)mainResultSet.getInt(2);
	                        	 	        	point = (STRUCT)mainResultSet.getObject(3);
	                        					geom = sdoAdapter.importGeometry( point );
	                        	      			if ( (geom instanceof oracle.sdoapi.geom.Point) )
	                        	      			{
	                        						oracle.sdoapi.geom.Point point0 = (oracle.sdoapi.geom.Point) geom;
	                        						int X = (int)point0.getX();
	                        						int Y = (int)point0.getY();
	                        						
	                        							g.setColor(Color.RED);
	                        							g.drawOval(X-raduis, Y-raduis, 2*raduis, 2*raduis);
		                        					    g.fillRect(X-7, Y-7, 15, 15);
	                        					
	                        					}

	                        	       	    }
	                        	 	        
	                        	 	        repaint();
	                        	        }
	                        			catch( Exception e )
	                        		    { System.out.println(" Error : " + e.toString() ); }
	                        	  }
	                        	  if(isB){
	                        		  STRUCT polygon;		//Structure to handle Geometry Objects
	                        		  Geometry geom;     	//Structure to handle Geometry Objects
                                      sql.append("\nSELECT * FROM BUILDING B WHERE sdo_relate(B.BZONE,"                          
                                              +"SDO_GEOMETRY("                      
  	                        				+ "2003,"                            
                                              + "NULL,"                     
  	                        				+ "NULL,"                     
                                              + "SDO_ELEM_INFO_ARRAY(1,1003,1),"                    
  	                        				+ "SDO_ORDINATE_ARRAY("                     
                                              + pointstr+")), "                    
  	                        				+ "\'mask=anyinteract\') = \'TRUE\'\n");
	                        			try
	                        			{
	                        	                                // shows result of the query
	              
	                        		        ResultSet mainResultSet=stat.executeQuery("SELECT * FROM BUILDING B WHERE sdo_relate(B.BZONE,"                          
	                                                +"SDO_GEOMETRY("                      
	    	                        				+ "2003,"                            
	                                                + "NULL,"                     
	    	                        				+ "NULL,"                     
	                                                + "SDO_ELEM_INFO_ARRAY(1,1003,1),"                    
	    	                        				+ "SDO_ORDINATE_ARRAY("                     
	                                                + pointstr+")), "                    
	    	                        				+ "\'mask=anyinteract\') = \'TRUE\'");
	                        		      
	                        		  		GeometryAdapter sdoAdapter = OraSpatialManager.getGeometryAdapter("SDO", "9",STRUCT.class, null, null, conn);
	                        		  		while(mainResultSet.next()){
	                        		  		polygon=(STRUCT)mainResultSet.getObject(4);
	                        		  		geom = sdoAdapter.importGeometry( polygon );
	                        		  		if ( (geom instanceof oracle.sdoapi.geom.Polygon) ){
	                        		  			oracle.sdoapi.geom.Polygon polygon0 = (oracle.sdoapi.geom.Polygon) geom;
	                        		  			for (Enumeration e = polygon0.getRings(); e.hasMoreElements();) 
	                        		  			{
	                        		  				
	                        		  					LineString lineString = (LineString)e.nextElement();
	                        		  					
	                        		  					
	                        		  				CoordPoint[] coordArray = lineString.getPointArray();
	                        		  				int[] contour_x = new int[coordArray.length];
	                        		  				int[] contour_y = new int[coordArray.length];
	                 
	                        		  				for (int i = 0; i < coordArray.length; i++)  //extract each vertex of the polynon
	                        		  				{
	                        		  						g.setColor(Color.yellow);
	                        		  						contour_x[i] = (int) coordArray[i].getX();
	                        		  						contour_y[i]= (int) coordArray[i].getY();
	                        		  					if(i>0){
	                        		  						g.drawLine(contour_x[i], contour_y[i], contour_x[i-1], contour_y[i-1]);
	                        		  					}
	                        		  				}
	                        		  			}
	                        		  		}
	                        		  	}
	                        			}catch( Exception e )
	                        		    { System.out.println(" Error : " + e.toString() ); }
	                        	  }
	                        	  if(isStu){
	                        		    STRUCT point;		//Structure to handle Geometry Objects
	                        			Geometry geom;     	//Structure to handle Geometry Objects
                                        
	                        			sql.append("\nSELECT * FROM STUDENT B WHERE sdo_relate(B.STULOC,"                          
                                                +"SDO_GEOMETRY("                      
    	                        				+ "2003,"                            
                                                + "NULL,"                     
    	                        				+ "NULL,"                     
                                                + "SDO_ELEM_INFO_ARRAY(1,1003,1),"                    
    	                        				+ "SDO_ORDINATE_ARRAY("                     
                                                + pointstr+")), "                    
    	                        				+ "\'mask=anyinteract\') = \'TRUE\'\n");
	                        			try
	                        			{
	                        	                                // shows result of the query

	                        		        ResultSet mainResultSet=stat.executeQuery("SELECT * FROM STUDENT B WHERE sdo_relate(B.STULOC,"                          
	                                                +"SDO_GEOMETRY("                      
	    	                        				+ "2003,"                            
	                                                + "NULL,"                     
	    	                        				+ "NULL,"                     
	                                                + "SDO_ELEM_INFO_ARRAY(1,1003,1),"                    
	    	                        				+ "SDO_ORDINATE_ARRAY("                     
	                                                + pointstr+")), "                    
	    	                        				+ "\'mask=anyinteract\') = \'TRUE\'");

	                        		  		GeometryAdapter sdoAdapter = OraSpatialManager.getGeometryAdapter("SDO", "9",STRUCT.class, null, null, conn);

	                        	 	        while( mainResultSet.next() )
	                        	    	    {
	                        		    	    point = (STRUCT)mainResultSet.getObject(2);
	                        					geom = sdoAdapter.importGeometry( point );
	                        	      			if ( (geom instanceof oracle.sdoapi.geom.Point) )
	                        	      			{
	                        						oracle.sdoapi.geom.Point point0 = (oracle.sdoapi.geom.Point) geom;
	                        						int X = (int)point0.getX();
	                        						int Y = (int)point0.getY();
	                        						g.setColor(Color.GREEN);
	                        						g.fillRect(X-5, Y-5, 10, 10);
	                        					}

	                        	       	    }
	                        	        }
	                        			catch( Exception e )
	                        		    { System.out.println(" Error : " + e.toString() ); }

	                        			System.out.println();
	                        	  }
	                        	  
	                        	  sql3=sql.toString();
	                        	  repaint();
	                        	  //polygonarray.clear();
	                        	  //HW2.mr.clear();
	                        	  break; 
	                          case 4:	                        	  
	                        	g.setColor(Color.BLUE);
	                        	g.fillRect(ssp_x-5, ssp_y-5, 10, 10);
                      		    STRUCT point,point1;		//Structure to handle Geometry Objects
                      			Geometry geom,geom1;     	//Structure to handle Geometry Objects
                      			int center_x=0,center_y=0,r=0;
                      			int raduis8;
                                sql.append("\nSELECT * FROM ASYS A WHERE SDO_NN(A.ASZONE,SDO_GEOMETRY(2001, NULL, SDO_POINT_TYPE("+ssp_x+","+ ssp_y+", NULL), NULL, NULL), 'sdo_num_res=1') = 'TRUE'\n");
                      			try
                      			{

                      		        ResultSet mainResultSet=stat.executeQuery("SELECT * FROM ASYS A WHERE SDO_NN(A.ASZONE,SDO_GEOMETRY(2001, NULL, SDO_POINT_TYPE("+ssp_x+","+ ssp_y+", NULL), NULL, NULL), 'sdo_num_res=1') = 'TRUE'");
                      		       
                      		  		GeometryAdapter sdoAdapter = OraSpatialManager.getGeometryAdapter("SDO", "9",STRUCT.class, null, null, conn);

                      		  		int i=0;
                      	 	        while( mainResultSet.next() )
                      	    	    {
                      	 	        	i++;
                      	 	        	raduis8 = (int)mainResultSet.getInt(2);
                      	 	        	point = (STRUCT)mainResultSet.getObject(3);
                      					geom = sdoAdapter.importGeometry( point );
                      	      			if ( (geom instanceof oracle.sdoapi.geom.Point) )
                      	      			{
                      						oracle.sdoapi.geom.Point point0 = (oracle.sdoapi.geom.Point) geom;
                      						int X = (int)point0.getX();
                      						int Y = (int)point0.getY();
                      						if(i==1){
                      							center_x=X;center_y=Y;
                      							r=raduis8;
                      							g.setColor(Color.RED);
                      							g.drawOval(X-raduis8, Y-raduis8, 2*raduis8, 2*raduis8);
	                        					g.fillRect(X-7, Y-7, 15, 15);
                      						}
                      						
                      					}

                      	       	    }
                      	 	     sql.append("\nSELECT * FROM STUDENT a "
	                        		        + "WHERE SDO_WITHIN_DISTANCE(a.STULOC, SDO_GEOMETRY(2001, NULL, SDO_POINT_TYPE("+ center_x+ ", " + center_y + ", NULL), NULL, NULL), 'distance="+r+"') = 'TRUE' \n");
                      	 	     ResultSet mainResultSet1=stat.executeQuery("SELECT * FROM STUDENT a "
	                        		        + "WHERE SDO_WITHIN_DISTANCE(a.STULOC, SDO_GEOMETRY(2001, NULL, SDO_POINT_TYPE("+ center_x+ ", " + center_y + ", NULL), NULL, NULL), 'distance="+r+"') = 'TRUE' ");
                      	 	     GeometryAdapter sdoAdapter1 = OraSpatialManager.getGeometryAdapter("SDO", "9",STRUCT.class, null, null, conn);
                      	 	     while( mainResultSet1.next() ){
                      	 	    	   point1 = (STRUCT)mainResultSet.getObject(2);
                      	 	    	   geom1 = sdoAdapter1.importGeometry(point1);
                      	 	    	if ( (geom1 instanceof oracle.sdoapi.geom.Point) ){
                      	 	    		oracle.sdoapi.geom.Point pointx = (oracle.sdoapi.geom.Point) geom1;
                  						int X1 = (int)pointx.getX();
                  						int Y1 = (int)pointx.getY();
                  						g.setColor(Color.GREEN);
                  						g.fillRect(X1-2, Y1-2, 5, 5);
                      	 	    	}
                      	 	        }   
                      	 	        sql4=sql.toString();
                      	 	        repaint();
                      	        }
                      			catch( Exception e )
                      		    { System.out.println(" Error : " + e.toString() ); }
	                        	  break;
	                          case 5:
	                       		    STRUCT point5,AScenter;		//Structure to handle Geometry Objects
	                       			Geometry geom5,geom55;     	//Structure to handle Geometry Objects
	                       			String ASname=null;
	                       			int AS_R;
	                       		
	                       		    
	                       			sql5="SELECT s.*, a2.* FROM STUDENT s, ASYS a1, ASYS a2"
	                                        +" WHERE a1.ASID = (SELECT a.ASID FROM ASYS a"
	                                        +" WHERE SDO_NN(a.ASZONE, SDO_GEOMETRY(2001, NULL, SDO_POINT_TYPE("+eq_x+"," +eq_y+", NULL), NULL, NULL), 'sdo_num_res=1') = 'TRUE')"
	                                        +"AND SDO_WITHIN_DISTANCE(s.STULOC, a1.ASCENTER, 'distance='||TO_CHAR(a1.ASRADIUS)) = 'TRUE'"
	                                        +"AND a2.ASID <> a1.ASID"
	                                        +" AND SDO_NN(a2.ASZONE, s.STULOC, 'sdo_num_res=2') = 'TRUE'";
	                       			try
	                       			{

	                       		        ResultSet mainResultSet5=stat.executeQuery(sql5);
	                       	
	                       		       	                       		        
	                       		  		GeometryAdapter sdoAdapter5 = OraSpatialManager.getGeometryAdapter("SDO", "9",STRUCT.class, null, null, conn);
	                       		  	    GeometryAdapter sdoAdapter55 = OraSpatialManager.getGeometryAdapter("SDO", "9",STRUCT.class, null, null, conn);
	                       		  		while(mainResultSet5.next()){
	                       		  		point5 = (STRUCT)mainResultSet5.getObject(2);
	                       		  		AScenter = (STRUCT)mainResultSet5.getObject(5);
	                       		  		AS_R = (int)mainResultSet5.getInt(4);
	                       		  		ASname = (String)mainResultSet5.getString(3);
                      					geom5 = sdoAdapter5.importGeometry( point5 );
                      					geom55 = sdoAdapter55.importGeometry( AScenter );
                      			
                      					if(!colorMap.containsKey(ASname)){
                      					   colorMap.put(ASname, colors[colorchoice % colors.length]);
                      					   colorchoice++;
                      					}else{
                      				
                      					}
                      					

                      	      			if ( (geom5 instanceof oracle.sdoapi.geom.Point)&& (geom55 instanceof oracle.sdoapi.geom.Point))
                      	      			{
                      	      			  
                      	      				oracle.sdoapi.geom.Point pointx5 = (oracle.sdoapi.geom.Point) geom5;
                      	      			    oracle.sdoapi.geom.Point pointx55 = (oracle.sdoapi.geom.Point) geom55;
                      						int X5 = (int)pointx5.getX();
                      						int Y5 = (int)pointx5.getY();
                      						int X55 = (int)pointx55.getX();
                      						int Y55 = (int)pointx55.getY();
                      						g.setColor(colorMap.get(ASname));
                      						g.fillRect(X5-5, Y5-5, 10, 10);
                      						g.fillRect(X55-7, Y55-7, 15, 15);
                      						g.drawOval(X55-AS_R, Y55-AS_R, 2*AS_R, 2*AS_R);
                      	      			}
	                       		  		}
 
                                        sql5="\n"+sql5+"\n";
	                       	 	        
	                       	 	        repaint();
	                       	        }
	                       			catch( Exception e )
	                       		    { System.out.println(" Error : " + e.toString() ); }
	                        	  
	                        	  
	                        	  repaint();

	                        	  break;
	                          case 6:
	                        	  g.setColor(Color.RED);
	                        	  g.fillRect(pq_x-2, pq_y-2, 5, 5);
	                    		  g.drawOval(pq_x-50, pq_y-50, 100, 100);
	                    		  repaint();
	                        	  break;
	                          case 7:
	                        	  for(int i=0;i<polygonarray.size();i++){
	                        		  g.setColor(Color.RED);
	                        		 if(i>0){	                        		
	                        		  g.drawLine(polygonarray.get(i).x, polygonarray.get(i).y, polygonarray.get(i-1).x, polygonarray.get(i-1).y);
	                        		 }
	                        		 if(i==polygonarray.size()-1){
	                        		  g.drawLine(polygonarray.get(i).x, polygonarray.get(i).y, polygonarray.get(0).x, polygonarray.get(0).y); 
	                        		 }
	                        	  }
	                        	  repaint();
	                        	  break;
	                          case 8:
	                        		g.setColor(Color.BLUE);
	 	                        	g.fillRect(eq_x-5, eq_y-5, 10, 10);
	                       		    STRUCT point8;		//Structure to handle Geometry Objects
	                       			Geometry geom8;     	//Structure to handle Geometry Objects
	                       			try
	                       			{

	                       		        ResultSet mainResultSet8=stat.executeQuery("SELECT * FROM ASYS A WHERE SDO_NN(A.ASZONE,SDO_GEOMETRY(2001, NULL, SDO_POINT_TYPE("+eq_x+","+ eq_y+", NULL), NULL, NULL), 'sdo_num_res=1') = 'TRUE'");
	                       		     
	                       		  		GeometryAdapter sdoAdapter8 = OraSpatialManager.getGeometryAdapter("SDO", "9",STRUCT.class, null, null, conn);

	                       	 	        while( mainResultSet8.next() )
	                       	    	    {
	                       	 	        	raduis8 = (int)mainResultSet8.getInt(2);
	                       	 	        	point8 = (STRUCT)mainResultSet8.getObject(3);	                  
	                       					geom8 = sdoAdapter8.importGeometry( point8 );
	                       	      			if ( (geom8 instanceof oracle.sdoapi.geom.Point) )
	                       	      			{
	                       						oracle.sdoapi.geom.Point point10 = (oracle.sdoapi.geom.Point) geom8;
	                       						int X8 = (int)point10.getX();
	                       						int Y8 = (int)point10.getY();
	                       				
	                       							g.setColor(Color.RED);
	                       							g.drawOval(X8-raduis8, Y8-raduis8, 2*raduis8, 2*raduis8);
	 	                        					g.fillRect(X8-7, Y8-7, 15, 15);
	                       					
	                       						
	                       					}

	                       	       	    }
	                       	 	        
	                       	 	        repaint();
	                       	        }
	                       			catch( Exception e )
	                       		    { System.out.println(" Error : " + e.toString() ); }
	                        		 repaint();
	                        	  break;
	                          case 9:
	                        	  MousePoint mp = new MousePoint();
	                        	  mp.x=p_x;
	                        	  mp.y=p_y;
	                        	  pointXY.add(mp);
	                        	  //int i = 0;
	                        	  //pointXY.get(pointXY.size()-1);
	                        	  
	                        	  g.setColor(Color.BLUE);
	                      
	                        	  g.fillRect(pointXY.get(pointcount).x-2, pointXY.get(pointcount).y-2, 5, 5);
	                        	  System.out.print("\n"+pointXY.get(pointcount).x+","+pointXY.get(pointcount).y);
	                        	  System.out.print("count:"+pointcount);
	                        	  pointcount++;
	                        
	                              repaint();
	                        	  
	                        	  break;
	                          default:
	                        	  break;
	                        	  }
	                          
	                          
                            }             	                        
	                          
           }

class MousePoint{
	int x;
	int y;
}
