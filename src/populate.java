import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

public class populate
{

	//Connection Variables
    Connection mainConnection;
  	Statement mainStatement = null;
  	ResultSet mainResultset1 = null;


  	public static void main(String[] args) throws IOException, SQLException
    {
    	populate e = new populate();
    	//e.populateStudent(mainConnection);
    }

	/********************************************/
	/* constructore								*/
	/**
	 * @throws SQLException 
	 * @throws IOException ******************************************/
 	public populate() throws IOException, SQLException
    {
		System.out.println();
		ConnectToDB();
		//this.populateStudent(ConnectToDB());
	}


	/********************************************/
	/* Connecting to DB							*/
	/**
	 * @throws SQLException 
	 * @throws IOException ******************************************/
	public void ConnectToDB() throws IOException, SQLException
 	{
		try
		{
			// loading Oracle Driver
    		System.out.print("Looking for Oracle's jdbc-odbc driver ... ");
	    	DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
	    	System.out.println(", Loaded.");

			String URL = "jdbc:oracle:thin:@localhost:1521:kangwang";
	    	String userName = "wk_test";
	    	String password = "wk523";

	    	System.out.print("Connecting to DB...");
	    	mainConnection = DriverManager.getConnection(URL, userName, password);
	    	System.out.println(", Connected!");

    		mainStatement = mainConnection.createStatement();
    		
   		}
   		catch (Exception e)
   		{
     		System.out.println( "Error while connecting to DB: "+ e.toString() );
     		e.printStackTrace();
     		System.exit(-1);
   		}
		
	/*-----------Execute Insertion--------------*/
		populateStudent(mainConnection);
		populateBuilding(mainConnection);
		populateAS(mainConnection);
 	}
	
	
	/*-----------Read File Function--------------*/
	private static ArrayList<String> readxyFile(String Filename){
		try{
			File xyfile = new File(Filename);
			Scanner s = new Scanner(xyfile);
			ArrayList<String> records =new ArrayList<String>();
			
			while(s.hasNext()){
				records.add(s.nextLine());
			}
			
			s.close();
			return records;
		}catch(FileNotFoundException e){
			System.out.println(e.toString());
			System.exit(-1);
		}
		return null;
	}
	

	/*-----------Inserting data into Table Student--------------*/
	private static void populateStudent(Connection conn) throws IOException, SQLException{
		ArrayList<String> students;
		PreparedStatement ps_stu=null,ps_stu_del=null;
		String str;
		//Boolean isDel = false;
		students = readxyFile("data/students.xy");
		
		/*DELETION BEFORE INSERTION*/
		ps_stu_del=conn.prepareStatement("DELETE FROM STUDENT");
		ps_stu_del.executeUpdate();
		ps_stu_del.close();
		System.out.println("Table Student has been cleared!");
	
		for(int i=0;i<students.size();i++){
			str = students.get(i);
			//System.out.println(str);
			String[] stuinfo=str.split(",\\s*");
			ps_stu=conn.prepareStatement("Insert into STUDENT values(?,SDO_GEOMETRY(2001,NULL,SDO_POINT_TYPE(?,?,NULL), NULL, NULL))");
			ps_stu.setString(1,stuinfo[0]);
			ps_stu.setInt(2, Integer.parseInt(stuinfo[1]));
			ps_stu.setInt(3, Integer.parseInt(stuinfo[2]));
			ps_stu.executeUpdate();
			
		}
		
		ps_stu.close();    
        System.out.println("students.xy has been loaded into Oracle database!");		
	}
	
	
	/*-----------Inserting data into Table Building--------------*/
	private static void populateBuilding(Connection conn) throws IOException, SQLException{
		ArrayList<String> building;
		PreparedStatement ps_building=null,ps_building_del=null;
		String str,nodeset=null;
		building = readxyFile("data/buildings.xy");
		
		/*DELETION BEFORE INSERTION*/
		ps_building_del=conn.prepareStatement("DELETE FROM BUILDING");
		ps_building_del.executeUpdate();
		ps_building_del.close();
		System.out.println("Table BUILDING has been cleared!");
		
		for(int i=0;i<building.size();i++){
	
			str = building.get(i);
			//System.out.println(str);
			String[] buildinginfo=str.split(",\\s*");
			StringBuilder temp = new StringBuilder();
			for(int j=3;j<buildinginfo.length;j++){
				//System.out.println(buildinginfo[j]);
				temp.append(buildinginfo[j]+",");
			}
			nodeset=temp.toString()+buildinginfo[3]+","+buildinginfo[4];
			//System.out.println(nodeset);
			//(BID, BNAME, VERTEX_NUM, BZONE)
			ps_building=conn.prepareStatement("INSERT INTO BUILDING VALUES(?, ?, ?, "
					+ "SDO_GEOMETRY(2003, NULL, NULL,"
					+ "SDO_ELEM_INFO_ARRAY(1,1003,1),"
                    + "SDO_ORDINATE_ARRAY("+nodeset+")))");
			ps_building.setString(1,buildinginfo[0]);
			ps_building.setString(2,buildinginfo[1]);
			ps_building.setInt(3, Integer.parseInt(buildinginfo[2]));
			ps_building.executeUpdate();
		}
		ps_building.close();
		System.out.println("buildings.xy has been loaded into Oracle database!");
	}
	
	
	/*-----------Inserting data into Table ASYS--------------*/
	private static void populateAS(Connection conn) throws IOException, SQLException{
		ArrayList<String> as;
		PreparedStatement ps_as=null,ps_as_del=null;
		String str;
		int X,Y,R;
		as = readxyFile("data/announcementSystems.xy");

		ps_as_del=conn.prepareStatement("DELETE FROM ASYS");
		ps_as_del.executeUpdate();
		ps_as_del.close();
		System.out.println("Table ASYS has been cleared!");
		
		for(int i=0;i<as.size();i++){
			str = as.get(i);
			//System.out.println(str);
			String[] asinfo=str.split(",\\s*");
			X=Integer.parseInt(asinfo[1]);
			Y=Integer.parseInt(asinfo[2]);
			R=Integer.parseInt(asinfo[3]);
			ps_as=conn.prepareStatement("INSERT INTO ASYS VALUES(?, ?,"+ 
		"SDO_GEOMETRY(2001, NULL, SDO_POINT_TYPE(?, ?, NULL), NULL, NULL),"+ 
		"SDO_GEOMETRY(2003, NULL, NULL, SDO_ELEM_INFO_ARRAY(1, 1003, 4), SDO_ORDINATE_ARRAY(?, ?, ?, ?, ?, ?)))");
			ps_as.setString(1,asinfo[0]); //AS_ID
			ps_as.setInt(2, R); //Radius
			ps_as.setInt(3, X); //X
			ps_as.setInt(4, Y); //Y
			ps_as.setInt(5, X);
			ps_as.setInt(6,Y-R);
			ps_as.setInt(7,X+R);
			ps_as.setInt(8, Y);
			ps_as.setInt(9, X);
			ps_as.setInt(10,Y+R);
			ps_as.executeUpdate();
        }
		
		ps_as.close();
		System.out.println("announcementSystems.xy has been loaded into Oracle database!");
	}

}

