# VGrid-Android
   VGrid-Android is a dynamic and customizable data grid solution built for Android applications using Jetpack Compose. It enables developers to display JSON data in a structured table format while providing powerful features such as column filtering, sorting, pagination, advanced search, and a column chooser for enhanced usability.

Key Features:

    Table View for JSON Data : Displays structured data in a tabular format with a clean UI.
    Column Filters: Allows users to filter data by specific column values.
    Column Sorting: Enables ascending/descending sorting on each column.
    Table Pagination: Supports paginated data display for improved performance.
    Advanced Search: Provides multiple search operators for filtering values within a single column.
    Column Chooser: Allows users to select and customize visible columns in the table.

Technology Stack:

    Language: Kotlin
    UI Framework: Jetpack Compose
    Data Handling: JSON Parsing
    State Management: ViewModel, LiveData/StateFlow

VGrid-Android simplifies complex data visualization in Android applications, ensuring a smooth user experience with its interactive and flexible table functionalities

# How To Create VGrid
   
ColumnModel:

    VGrid have the ColumnModel class and it is used to define the columns.
    data class ColumnModel(
    var index: Int,       // Column index for positioning
    var label: String,    // Display name in the table
    var name: String,     // Corresponding JSON field name
    var hidden: Boolean,  // Whether the column is hidden
    var width: Dp,        // Column width
    var sorttype: String, // Sorting type (Number, String, Float, Date)
    var height: Dp,       // Column height (should be uniform)
    var sortable: Boolean,// Is sorting enabled?
    var editable: Boolean,// Is column editable?
    var format: String,   // Format type (e.g., number, string)
    var hide_column: Boolean, // Hide from column chooser?
    var formattype: String // Display format (Text, EditText)
    )
    
Using the list Of ColumnModel to define our table configuration


      val tableConfiguration = listOf(
          ColumnModel(0, "Sno", "sno", true, 80.dp, "number", 32.dp, true, false, "number", false, "Text"),
          ColumnModel(1, "Emp ID", "empid", false, 180.dp, "number", 32.dp, true, false, "number", true, "Text"),
          ColumnModel(2, "Name", "empname", false, 180.dp, "string", 32.dp, true, false, "string", true, "Text"),
          ColumnModel(3, "Role", "emprole", false, 180.dp, "string", 32.dp, true, false, "String", true, "Text"),
          ColumnModel(4, "Salary", "empsalary", false, 140.dp, "float", 32.dp, true, false, "number", true, "Text"),
          ColumnModel(5, "Join Date", "empjdate", false, 120.dp, "date", 32.dp, true, true, "string", true, "Text"),
          ColumnModel(6, "Notes", "notes", false, 180.dp, "string", 32.dp, true, true, "string", true, "EditText"),
          ColumnModel(7, "Mark", "mark", false, 120.dp, "string", 32.dp, true, false, "string", true, "Text")
      )

Create a variable to set the color of the Grid

      var gridColors: MutableList<Pair<String, Color>> = mutableListOf(
          Pair("primary_color", Color(0xFF993366)),  
          Pair("secondary_color", Color.White),     
          Pair("third_color", Color.Black),         
          Pair("fourth_color", Color.Gray)          
      )

Create a list that allows users to choose how many rows to display per page

      var listOfRows = arrayOf(10, 20, 30, 40, 50)

Set Table Height and Width

      var tableHeight = "auto"  // Options: "auto" or a specific value like "400"
      var tableWidth = "auto"   // Options: "auto" or a specific value like "800"

Set The Input File Path of Json File 

      val musicDir = application.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
      var input_json_file = File(musicDir,"sample.json").toString()


      
Create a object for jqgridview with previous options then call the Main_screen function from jqgrid object to render the VGrid.

        var grid = jqgridview(
                        applicationContext,
                        modifier = Modifier.padding(innerPadding),
                        tableconfiguration,
                        input_json_file,
                        listofrows,
                        gridcolors,
                        tableheight,
                        tablewidth
               )
      grid.Main_screen(activity = LocalContext.current as Activity)

Sample Code

           var tableconfiguration = listOf(
                        ColumnModel( 0, "Sno", "sno", true, 80.dp, "number", 32.dp, true, false, "number", false, "Text" ),
                        ColumnModel( 1, "Emp ID", "empid", false, 180.dp, "number", 32.dp, true, false, "number", true, "Text" ),
                        ColumnModel( 2, "Name", "empname", false, 180.dp, "string", 32.dp, true, false, "string", true, "Text" ),
                        ColumnModel( 3, "Role", "emprole", false, 180.dp, "string", 32.dp, true, false, "String", true, "Text" ),
                        ColumnModel( 4, "Salary", "empsalary", false, 140.dp, "float", 32.dp, true, false, "number", true, "Text" ),
                        ColumnModel( 5, "Join Date", "empjdate", false, 120.dp, "date", 32.dp, true, true, "string", true, "Text" ),
                        ColumnModel( 6, "Notes", "notes", false, 180.dp, "string", 32.dp, true, true, "string", true, "EditText" ),
                        ColumnModel( 7, "Mark", "mark", false, 120.dp, "string", 32.dp, true, false, "string", true, "Text" )
                    )
                    var gridcolors: MutableList<Pair<String, Color>> = mutableListOf()
                    gridcolors.add(Pair("primary_color",Color(0xFF993366)))
                    gridcolors.add(Pair("secondary_color",Color.White))
                    gridcolors.add(Pair("third_color",Color.Black))
                    gridcolors.add(Pair("fourth_color",Color.Gray))
                    var listofrows = arrayOf(10,20,30,40,50)
                    var tableheight = "auto"
                    var tablewidth = "auto"
                    var grid = jqgridview(
                        applicationContext,
                        modifier = Modifier.padding(innerPadding),
                        tableconfiguration,
                        copiedFilePath,
                        listofrows,
                        gridcolors,
                        tableheight,
                        tablewidth
                    )
                    grid.Main_screen(activity = LocalContext.current as Activity)
      
# Sample Output
![Screenshot from 2025-02-13 14-18-21](https://github.com/user-attachments/assets/32ce8596-6229-4d76-bb0a-89cfd9a7cd85)

