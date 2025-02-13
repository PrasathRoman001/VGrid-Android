package com.vgrid_android.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vgrid_android.Common.Add_notes
import com.vgrid_android.Common.Add_to_photos
import com.vgrid_android.Common.Cancel
import com.vgrid_android.Common.ClipboardCopy
import com.vgrid_android.Common.Delete_sweep
import com.vgrid_android.Common.Device_reset
import com.vgrid_android.Common.Enter
import com.vgrid_android.Common.Keyboard_double_arrow_down
import com.vgrid_android.Common.Keyboard_double_arrow_left
import com.vgrid_android.Common.Keyboard_double_arrow_right
import com.vgrid_android.Common.Keyboard_double_arrow_up
import com.vgrid_android.Common.Manage_search
import com.vgrid_android.Common.Table
import com.google.gson.JsonArray
import com.patrik.fancycomposedialogs.dialogs.ErrorFancyDialog
import com.patrik.fancycomposedialogs.dialogs.InformativeFancyDialog
import com.patrik.fancycomposedialogs.enums.DialogActionType
import com.vgrid_android.Common.ColumnModel
import com.vgrid_android.viewmodel.TableViewModel
import java.io.File
import java.math.BigDecimal
import java.util.regex.Pattern


// create a data class for store the search filters
data class SearchFilter(val colname : String, var svalue : String, var operator: String,var sort : String,var sorttype:String,var sortmethod:String)


class jqgridview(context: Context, modifier: Modifier, tableconfig:List<ColumnModel>, inputfile:String, private var paginationArray: Array<Int>?,var vgridcolors: MutableList<Pair<String,Color>>,var tableheight: String,var tablewidth: String) {
    var primary_color = Color(0xFF993366)
    //var primary_color = Color(0xFF0CC7E4)
    var secondary_color= Color.White
    var black=Color.Black
    var gray = Color.Gray
    var white = Color.White
    var red=Color.Red
    var blue=Color.Blue
    var context=context
    var tableconfig: List<ColumnModel> = tableconfig
    var input_file=inputfile


    //create a variable for search filters
    var searchFilters = mutableListOf<SearchFilter>()
    private val defaultPagination = arrayOf(10, 20, 30, 50) // Default pagination values
    // create a variable to show the paginations
    //var pagination = arrayOf(5, 10, 15)

    var pagination: Array<Int>? =null
    init {
        if (paginationArray.isNullOrEmpty()) {
            pagination = defaultPagination
        }
        else{
            pagination=paginationArray
        }
        if(!vgridcolors.isEmpty()){
            vgridcolors.forEach {  pair ->
                if(pair.first == "primary_color" && pair.second != null){
                    primary_color = pair.second
                }
                if(pair.first == "secondary_color" && pair.second != null){
                    secondary_color = pair.second
                }
                if(pair.first == "third_color" && pair.second != null){
                    black = pair.second
                }
                if(pair.first == "fourth_color" && pair.second != null){
                    gray = pair.second
                }
                if(pair.first == "white" && pair.second != null){
                    white = pair.second
                }
            }
        }

    }
    //===  FUNCTION  ================================================================
//  NAME        :   Main_screen
//  PURPOSE     :   this the main function of jqgrid change the screen orientation and call the jqgrid composable function
//  PARAMETERS  :   Activity,Call back function,call back function
//  RETURNS     :   NULL
//  DESCRIPTION :   this the main function of jqgrid change the screen orientation and call the jqgrid composable function
//================================================================================
    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun Main_screen(activity: Activity) {

        tableconfig.forEach { col ->
            searchFilters.add(SearchFilter(col.name,"","","","",""))
        }

        // create a surface to occupied full screen
        //
        val configuration = LocalConfiguration.current
        var screenWidth = configuration.screenWidthDp.dp
        var screenHeight = configuration.screenHeightDp.dp

        if(tableheight.toLowerCase() != "auto"){
            screenHeight= tableheight.toIntOrNull()?.dp!!
        }else{
            screenHeight-=30.dp
        }
        if(tablewidth.toLowerCase() != "auto"){
            screenWidth= tablewidth.toIntOrNull()?.dp!!
        }
        else{
            screenWidth-=60.dp
        }
        println("Screen Height :"+screenHeight)
        println("Screen Width :"+screenWidth)
        Surface(modifier = Modifier
            .padding(start = 20.dp, end = 10.dp, top = 10.dp, bottom = 10.dp) // Add padding
            .height(screenHeight)
            .width(screenWidth)
        ) {
            var file = File(input_file)
            if(file.exists()){
                // call the home screen function to start create a jqgrid design
                HomeScreen(activity,modifier = Modifier,tableconfig,pagination!!)
            }else{
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
                    Text("Given Json File Is Not Exist - $input_file", fontSize = 32.sp)
                }
            }
        }
    }



    //===  FUNCTION  ================================================================
//  NAME        :   wrap_content
//  PURPOSE     :   this function is used to measure the column text size
//  PARAMETERS  :   Float
//  RETURNS     :   Int
//  DESCRIPTION :   this function is used to measure the column text size and return the line count
//================================================================================
    fun wrap_content(width: Float): Int {
        var default_character_width = 8.dp.value
        return (width / default_character_width).toInt()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun HomeScreen(
        activity: Activity,
        modifier: Modifier,
        tableconfig: List<ColumnModel>,
        pagination: Array<Int>,
        tableViewModel: TableViewModel = viewModel()
    ) {

        // Optionally, save and restore additional UI state using rememberSaveable
        //var someLocalState by rememberSaveable { mutableStateOf("some value") }
        // change the screen orientation to landscape
        // create a recompose for json data and it is contain the actuall data need to show in jqgrid
        //var filterData by remember { mutableStateOf() }
        //var filterData = JsonArray()

        // activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        val filterData by tableViewModel.tableData.collectAsState()

        val isLoading by tableViewModel.isLoading.collectAsState()
        //println("Filter Data :"+filterData)

        // Ensure JSON loads correctly
        LaunchedEffect(Unit) {
            // Delay the orientation change after JSON data is loaded

            // println("TESTING")
            tableViewModel.loadJsonFromFile(input_file)

        }


        //println("Filter Data :"+filterData)

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
        else {

            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

            // create a recompose ui flag for next and previous page
            var currentPage by remember { mutableStateOf(1) }

            // create  a recompose ui for jqgrid table configuration
            var tableconfiguration by remember { mutableStateOf(tableconfig) }

            // create a recompose ui for page size (how many data will show in a each page)
            var pagesize by remember { mutableStateOf(pagination[0]) }


            // declare a variable for offset calculation
            var offset = 0

            // check the current page is greaterthan one means change offset value from zero
            if (currentPage > 1) {
                // set the offset value is current page -1 and multiple with pagesize
                offset = (currentPage - 1) * pagesize;
            }

            // store the data size count value
            var total_rec = filterData!!.size()
            // get the total pages from the table data
            //var total_pages = filterData!!.size() / pagesize
            println("Offset value :"+offset)
            println("Current Page :"+currentPage)
            // create a column layout
            Column(modifier = modifier.fillMaxSize()) {
                // call the table screen function with call back functions to design the jqgrid
                TableScreen(tableconfiguration,
                    filterData!!,
                    total_rec,
                    pagesize,
                    offset,
                    pagination,
                    currentPage,
                    // onSearchFilter call back function purpose is column search
                    // user can search multiple column as well as
                    // alos it is perform the column sorting also.
                    onSearchFilter = { pattern ->
                        // call the fetch data function in database to get the data
                        //  using the search filters to apply column search value and column sort
                        // then get the data from data base and return as jsonArray
                        //filterData = JsonArray()
                        //filterData = dbManager.fetch_data(tablename,column_name,searchFilters,offset,pagesize+1,FLAG,condition)
                        tableViewModel.Search_Data(input_file,searchFilters,offset,pagesize+1)

                    },
                    // on Next page call back function purpose is manage next page event
                    onNextPage = {
                        // check the total data count is greater then page size means enable the next button action
                        if (total_rec >= pagesize) {
                            // increment the currentpage variable to recompose the table again
                            // with the next page data
                            currentPage++
                            // set the offset value
                            offset = 0
                            if (currentPage > 1) {
                                offset = (currentPage - 1) * pagesize;
                            }
                            // get the data from database then again recompose the table with new data
                            //filterData = dbManager.fetch_data(tablename,column_name,searchFilters,offset,pagesize+1,FLAG,condition)
                            tableViewModel.Search_Data(input_file,searchFilters,offset,pagesize+1)
                        }
                    },
                    // onPrevPage is used to controll the previouse page action
                    onPrevPage = {

                        // check the current page is greathan one
                        if (currentPage > 1) {
                            // true means decrement the currentppage to trigger the recompose
                            currentPage--

                            // calculate the offset
                            offset = 0
                            if (currentPage > 1) {
                                offset = (currentPage - 1) * pagesize;
                            }
                            // get the data from database then again recompose the jqgrid table with new data
                            //filterData = dbManager.fetch_data(tablename,column_name,searchFilters,offset,pagesize+1,FLAG,condition)
                            tableViewModel.Search_Data(input_file,searchFilters,offset,pagesize+1)
                        }
                    },

                    // on Update config is used for column chooser to show and remove the columns in the table
                    onUpdateConfig = { tableconf, index, flag, flag2 ->
                        // override the current configuration with emptylist
                        tableconfiguration = emptyList()
                        // next call the update_table_config to create a new configuration
                        tableconfiguration =
                            update_table_config(tableconfig, index = index, flag, flag2)

                        // next again get the data to recompose the table
                        //filterData = dbManager.fetch_data(tablename,column_name,searchFilters,offset,pagesize+1,FLAG,condition)

                    },
                    // it is used to update the page size i means it will update page size to configure a new value to how many records will be show
                    // on the each page
                    onRowUpdate = { it ->
                        // set the current page as 1
                        currentPage = 1
                        // set the new page size
                        pagesize = it
                        // set offset as zero
                        offset = 0
                        tableViewModel.Search_Data(input_file,searchFilters,offset,pagesize+1)
                        // get the data from database then recompose the table
                        //filterData = dbManager.fetch_data(tablename,column_name,searchFilters,offset,pagesize+1,FLAG,condition)
                    },
                    // onAdavanceSearch is used for adavanced searched operatorion
                    onAdavanceSearch = {
                        // set the current page
                        currentPage = 1
                        // set the page size
                        pagesize = pagesize
                        // set the offset
                        offset = 0
                        // get the data from database then recompose it
                        //filterData = dbManager.fetch_data(tablename,column_name,searchFilters,offset,pagesize+1,FLAG,condition)
                        tableViewModel.Search_Data(input_file,searchFilters,offset,pagesize+1)

                    },
                    onUpdateData={ row_value,col_name,col_value,row_id->
                        //tableViewModel.updateJsonValue(context,input_file,row_id,col_name,col_value)
                        tableViewModel.updateJsonValue(input_file,row_value,col_name,col_value,row_id)
                    }
                )
            }
        }
    }


    //===  FUNCTION  ================================================================
//  NAME        :   Tableheader
//  PURPOSE     :   this used to show the header column one by one based on the configuration
//  PARAMETERS  :   String,Dp,String,Int,call back function,Dp,String,Boolean,JsonArray
//  RETURNS     :   NULL
//  DESCRIPTION :   this used to show the header column one by one based on the configuration
//================================================================================
    @Composable
    fun Tableheader(
        text: String,
        weight: Dp,
        colname: String,
        index: Int,
        onSearchFilter: (MutableList<SearchFilter>) -> Unit,
        height: Dp,
        sorttype: String,
        sortable: Boolean,
        tabledata: JsonArray
    ) {

        // add a recomposable flag for sortting
        // ascending
        var asc_icon by remember { mutableStateOf(false) }
        // descending
        var desc_icon by remember { mutableStateOf(false) }

        // recompose for searchfilters
        var sort_filters by remember { mutableStateOf(searchFilters) }

        //  println("Sort Filters :"+searchFilters)

        // create a row layout
        Row(
            modifier = Modifier
                .height(height)
                .width(weight)
                .border(1.dp, black)
                .clickable {
                    // check the table data is empty or not
                    if (tabledata.size() > 0) {
                        // check the sortable is enabled or not
                        if (sortable) {
                            //sort_filters= emptyList<SearchFilter>().toMutableList()
                            // initailize the sort filtes empty for fist
                            tableconfig.forEach { index ->
                                //if(index.name != colname){
                                searchFilters[index.index].sort = ""
                                //sort_filters[index.index].sort = ""
                                //filters[index.index].sort=""
                                //}
                            }
                            var filters = searchFilters
                            // check asc is enabled or not
                            if (!asc_icon) {
                                // show the icon of ascending icon
                                asc_icon = true
                                // remove the icon of descending icon
                                desc_icon = false
                                // add search filter for data sorting
                                searchFilters[index].sort = "asc"
                                // sort_filters[index].sort = "asc"
                                filters[index].sort = "asc"
                                // set the current column sort method also
                                searchFilters[index].sortmethod = sorttype
                            } else {
                                // show the icon of descending
                                desc_icon = true
                                // remove the icon of ascending
                                asc_icon = false
                                // set the search filter for descending
                                searchFilters[index].sort = "desc"
                                //sort_filters[index].sort = "desc"
                                filters[index].sort = "desc"
                                // add the current column sort type
                                searchFilters[index].sortmethod = sorttype
                            }
                            sort_filters = emptyList<SearchFilter>().toMutableList()
                            sort_filters = filters

                            // pass the searchfilters to onSearchFilter call back function for recompose the table again
                            onSearchFilter(searchFilters)
                        }
                    }
                },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // show the column header text
            Text(
                text = text,
                Modifier.padding(6.dp),
                color = secondary_color, fontSize = 14.sp
            )
            Column() {

                // check the sort filtes is not null,empty
                if (!sort_filters.isNullOrEmpty()) {

                    // check the current column sort is not empty
                    // if(sort_filters[index].sort.isNotEmpty()){
                    // check the current column sort is ascending means show the asc icon
                    if (sort_filters[index].sort == "asc") {
                        // show the ascending icon
                        Icon(
                            imageVector = Keyboard_double_arrow_up,
                            //painter = painterResource(R.drawable.up),
                            contentDescription = "Asc",
                            modifier = Modifier.size(25.dp),
                            tint = secondary_color
                        )
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    // check the current column sort is descending means show the des icon
                    if (sort_filters[index].sort == "desc") {
                        // show the descending icon
                        Icon(
                            imageVector = Keyboard_double_arrow_down,
                            //painter = painterResource(R.drawable.down),
                            contentDescription = "Desc",
                            modifier = Modifier.size(25.dp),
                            tint = secondary_color
                        )
                    }
                    //}
                }
            }
        }
    }

    //===  FUNCTION  ================================================================
//  NAME        :   Tablebody
//  PURPOSE     :   it is used to show the each column data one by one row
//  PARAMETERS  :   String,Dp,Dp,String,Boolean,Int,String,Call back function,Call back function,String,Boolean,Call back function
//  RETURNS     :   NULL
//  DESCRIPTION :   it is used to show the each column data one by one row
//================================================================================


    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun Tablebody(
        text: String,
        weight: Dp,
        height: Dp,
        format: String,
        first_col: Int?,
        colname: String,
        editable:Boolean,
        format_type:String,
        first_col_name: String,
        onUpdateData: (Int, String, String, String) -> Unit
    ) {
        // check the current column format is update or not
        if (format == "update") {
            // create a row layout
            Row(
                Modifier
                    .width(weight)
                    .height(height)
                    .background(secondary_color, shape = RoundedCornerShape(8.dp))
                    .border(1.dp, black),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // create a icon button for update with onclick event
                // when user click the button call the Warranty update call back function
                IconButton(onClick = {
                    //Warranty_update(first_col!!)
                }) {
                    // create a icon update
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        "Update Button",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        // handle a rate or no values in decimal & folat values.
        else if (format == "number") {
            //println("Text :" + text)
            var pattern = "\\d+\\.\\d+"
            var matcher = Regex(pattern).find(text)
            //var formatno = if(matcher!=null) DecimalFormat("#,###.0").format(text.toDouble()) else  text
            var formatno = if (matcher != null) BigDecimal(text).toPlainString() else text
            // create a row layout
            Row(
                Modifier
                    .width(weight)
                    .height(height)
                    .background(secondary_color, shape = RoundedCornerShape(8.dp))
                    .border(1.dp, black),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatno,
                    Modifier
                        //.border(1.dp, black)
                        //.height(height)
                        //.width(weight)
                        .padding(6.dp),
                    fontSize = 14.sp
                )
            }
        } else {
            if(format_type == "Text") {
                // create a row layout
                Row(
                    Modifier
                        .width(weight)
                        .height(height)
                        .background(secondary_color, shape = RoundedCornerShape(8.dp))
                        .border(1.dp, black),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = text,
                        Modifier
                            //.border(1.dp, black)
                            //.height(height)
                            //.width(weight)
                            .padding(6.dp),
                        fontSize = 14.sp
                    )
                }
            }else if(format_type == "EditText"){
                var flag by remember { mutableStateOf(false) }
                if(flag) {
                    Notes_Bottom_Sheet(
                        onDismiss = { flag = false },
                        first_col,
                        colname,
                        text,
                        editable,
                        first_col_name,
                        onUpdateData
                    )
                }
                Row(
                    Modifier
                        .width(weight)
                        .height(height)
                        .background(secondary_color, shape = RoundedCornerShape(8.dp))
                        .border(1.dp, black),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { flag = true }) {
                        Icon(imageVector = Add_notes, "Add notes", tint = black)
                    }
                }
            }
        }
    }


    //===  FUNCTION  ================================================================
//  NAME        :   Notes_Bottom_Sheet
//  PURPOSE     :   it is used to show the outline text area for edit the comments feild
//  PARAMETERS  :   Call back function,call back function,Int,String,String,String,Int
//  RETURNS     :   NULL
//  DESCRIPTION :   it is used to show the outline text area for edit the comments feild
//================================================================================

    /*
    *  Notes_Bottom_Sheet(
                        onDismiss = { flag = false },
                        first_col,
                        colname,
                        text,
                        editable
                    )*/
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Notes_Bottom_Sheet(
        onDismiss: () -> Unit,
        //onUpdateData: (Int, String, String, String) -> Unit,
        first_col: Int?,
        colname: String,
        text1: String,
        editable: Boolean,
        first_col_name: String?,
        onUpdateData: (Int, String, String, String) -> Unit

        //flag: Int
    ){


        // create a bottom sheet state remember
        var modalBottomSheetState = rememberModalBottomSheetState()

        // create a  bottom sheet
        ModalBottomSheet(onDismissRequest = { onDismiss() }, sheetState = modalBottomSheetState, dragHandle = { BottomSheetDefaults.DragHandle()}, scrimColor = Color.Transparent) {
            // recompose flag for after user type in the text area
            // means this variable will update the UI
            var text by remember { mutableStateOf(text1)}
            var url_validation by remember { mutableStateOf(false)}

            // replace the "&" character as new line "\n"
            text=text.replace("&","\n")
            var clipboardManager = LocalClipboardManager.current
            // create a column layout
            Column(modifier = Modifier
                .fillMaxSize()
                .background(primary_color), horizontalAlignment = Alignment.CenterHorizontally) {
                // create a outlined text field
                OutlinedTextField(
                    value = text,
                    onValueChange = {
                        text=it
                        //onValueChange(it)
                    },
                    colors = TextFieldDefaults.colors(
                        //focusedTextColor = secondary_color,
                        cursorColor = black,
                        focusedLabelColor = secondary_color,
                        unfocusedLabelColor = if(text!="")secondary_color else black ,
                        focusedIndicatorColor = secondary_color,
                        focusedContainerColor = secondary_color,
                        unfocusedContainerColor = secondary_color
                        // unfocusedLabelColor = secondary_color
                    ),
                    // check the flag is 2 means set the text field as read only else set the text field as editable
                    readOnly = if(!editable) true else false,
                    // set the label
                    //label = { Text(if(flag ==2 ) "Product URL" else if(flag == 3) "Address" else if(flag == 4) "Visiting Purpose" else "Comments",style=TextStyle(fontSize = 16.sp)) },

                    label = { Text("$colname",style=TextStyle(fontSize = 16.sp)) },
                    //label = { Text(text = "$column_name")},
                    modifier = Modifier
                        .height(300.dp)
                        .width(600.dp)
                )
                // create a row layout
                Row(horizontalArrangement =Arrangement.Center, verticalAlignment = Alignment.CenterVertically){

                    // check the flag is not equal to means show the submit button else not show the button
                    if(editable){
                        // create a button for submit
                        Button(
                            onClick = {

                                // call the call back function to store the edit text into database
                                // also change the new line characters as "&"
                                // check the comment is not edited
                                if(text != text1.replace("&","\n")){
                                    onUpdateData(
                                        first_col!!,
                                        colname,
                                        text.replace("\n", "&"),
                                        first_col_name!!
                                    )
                                    // close the bottom sheet using the onDismiss() call back
                                    onDismiss()
                                }

                            },colors=ButtonDefaults.buttonColors(secondary_color), shape = RoundedCornerShape(8.dp)
                        ) {
                            // set the button text
                            Text("Submit",color=primary_color)
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                    }

                    // create a icon button for copy the text
                    IconButton(onClick = {
                        if(text!="")
                            clipboardManager.setText(AnnotatedString(text))
                    }) {
                        Icon(imageVector = ClipboardCopy,"Copy the text", tint = secondary_color)
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    // create a button for cancel
                    Button(

                        onClick = {
                            // close the bottom sheet
                            onDismiss()
                        },colors=ButtonDefaults.buttonColors(secondary_color), shape = RoundedCornerShape(8.dp)
                    ) {
                        // set the button text
                        Text("Cancel",color= primary_color)
                    }
                }
            }
        }
    }


    //===  FUNCTION  ================================================================
//  NAME        :   Column_Search
//  PURPOSE     :   this used to show search filed for each column
//  PARAMETERS  :   String,Dp,Int,String,JsonArray,Call back function,Dp,String,Boolean,Boolean
//  RETURNS     :   NULL
//  DESCRIPTION :   this used to show search filed for each column
//================================================================================
    @Composable
    fun Column_Search(
        text: String,
        weight: Dp,
        id: Int,
        col: String,
        tabledata: JsonArray,
        onSearchFilter: (MutableList<SearchFilter>) -> Unit,
        height: Dp,
        svalue: String,
        hidden: Boolean,
        sortable: Boolean
    ){
        //println("S value :"+svalue)
        // declare a recompose variable
        //println("Search Value :"+svalue+"Filter Value : ${searchFilters[id].svalue}")
        var textState by remember { mutableStateOf("") }

        LaunchedEffect(svalue) {
            textState = if (svalue.isNotEmpty()) svalue else ""
        }
        //  var textState by remember { mutableStateOf(if( svalue != "")svalue else "") }
        var newtext=if(searchFilters[id].svalue == "")"" else textState

//      println("New Text :"+newtext)
        //      println("Text State :"+textState)
        // check the current column is not hidden
        if(!hidden){
            // create a row layout
            Row(
                Modifier
                    .width(weight)
                    .height(height)
                    .background(secondary_color, shape = RoundedCornerShape(8.dp))
                    .border(1.dp, black)
                ,
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                var height : Dp=(height-10.dp)
                var weight : Dp = (weight-20.dp)
                // create text field
                BasicTextField(
                    //value = searchFilters[id].svalue,
                    value = textState,
                    onValueChange ={newvalue ->
                        // check the sortable is enabled or not for the current column
                        if(sortable){
                            // update the text value
                            textState=newvalue
                            // add search filter for the current column
                            searchFilters[id] = SearchFilter(col,newvalue,"cn","","","")
                            //  println("Search Filters :"+searchFilters)
                            // call back function to apply the filter then recompose the table
                            onSearchFilter(searchFilters)
                        }
                        //}
                    },
                    // maxLines = 1,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    // set readonly true when sortable is false
                    readOnly = if(sortable) false else true,
                    modifier = Modifier
                        .border(1.dp, black, shape = RoundedCornerShape(8.dp))
                        .height(height)
                        .width(weight)
                        .padding(6.dp),
                    textStyle = TextStyle(color = black, fontSize = 12.sp)
                )
            }
        }
    }



    //===  FUNCTION  ================================================================
//  NAME        :   TableScreen
//  PURPOSE     :   This function is draw the jqgrid
//  PARAMETERS  :   List<ColumnModel>,JsonArray,Int,Int,Int,Array<Int>,call back function,call back function,call back function,call back function,call back function,call back function,call back function,call back function,call back function
//  RETURNS     :   NULL
//  DESCRIPTION :   This function is draw the jqgrid
//================================================================================
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun TableScreen(
        tableconfig: List<ColumnModel>,
        tabledata: JsonArray,
        total_rec: Int,
        pagesize: Int,
        offset: Int,
        pagination: Array<Int>,
        currentPage : Int,
        onSearchFilter:(MutableList<SearchFilter>) -> Unit,
        onNextPage: () -> Unit,
        onPrevPage: () -> Unit,
        onUpdateConfig: (List<ColumnModel>, Int, Boolean,Boolean) -> Unit,
        onRowUpdate: (Int) -> Unit,
        onAdavanceSearch: () -> Unit,
        onUpdateData: (Int, String, String, String) -> Unit
    ){

        // create a Box layout
        Box(modifier = Modifier
            .fillMaxSize()
        ){
            Scaffold(
                // call the topbar composable functon to show the jqgrdi pagination part
                bottomBar = {
                    TopBar(modifier = Modifier,onNextPage,onPrevPage,tabledata,onUpdateConfig,pagesize,total_rec,offset,onRowUpdate,pagination,currentPage,onAdavanceSearch)
                }
            ){
                // create a lazy row for horizontal scroll
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                    item{
                        // call the write Hear composable function to show the jqgrid
                        //writeHeader(tableconfig, tabledata, onSearchFilter,onNextPage,onPrevPage,onUpdateConfig,onUpdateData,Warranty_update,pagesize,imagePreview)
                        // writeHeader(tableconfig, tabledata, onSearchFilter,onNextPage,onPrevPage,onUpdateConfig,pagesize)
                        writeHeader(tableconfig, tabledata, onSearchFilter,pagesize,onUpdateData)
                    }
                }
                // check the data is empty or not
                if(tabledata.isEmpty){
                    // show the text there is no records
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
                        Text("There is No records")
                    }
                }
            }
        }
    }


    //===  FUNCTION  ================================================================
//  NAME        :   find_max_lines
//  PURPOSE     :   This function is used for calulate the lines based on the input string
//  PARAMETERS  :   String,Int
//  RETURNS     :   Int
//  DESCRIPTION :   This function is used to count lines while wrap the text in the each column
// ================================================================================
    fun find_max_lines(input:String,max_char_length:Int) : Int{
        //declare the counter variables
        var line_count=0;
        var count=0;
        // process the character one by one
        for(character in input){

            // check the count is greater than max count
            if(count>max_char_length){
                // re-iniliaze the counter variable count
                count=0
                // increment the line count counter vairable
                line_count++
            }else{
                // count is less than max count so just increment the count variable
                count++
            }
        }
        // return the line count
        return line_count+1;
    }



    //===  FUNCTION  ================================================================
//  NAME        :   writeColumn
//  PURPOSE     :   this function is process the data and show it jqgird
//  PARAMETERS  :   List<ColumnModel>,JsonArray,Call back function,Call back function,Int,call back function
//  RETURNS     :   NULL
//  DESCRIPTION :   this function is process the data based one row by row and column by column
//                  also find the row height.
//================================================================================
    @RequiresApi(Build.VERSION_CODES.O)

    @Composable
    fun writeColumn(
        tableconfig: List<ColumnModel>,
        tabledata: JsonArray,
        pagesize: Int,
        onUpdateData: (Int, String, String, String) -> Unit
    ){
        // create a varaibel for store the row height
        var height :Dp? = tableconfig[0].height
        // check the table data is empty or not
        if(tabledata.isEmpty){
            println("Table data is Empty...!")
        }else{
            var row_count=0
            // table data is not empty
            // so process the table data row by row
            tabledata.forEach { row ->
                if (row_count <= pagesize - 1) {
                    // create a counter variable i with inintalize value zero
                    var i = 0

                    // store the first column value
                    // it is store the row sno number for update the notes,comments value in database
                    var first_col: Int? = null
                    var first_col_name: String? = null

                    // create a row layout
                    Row(
                        Modifier.background(secondary_color, shape = RoundedCornerShape(8.dp))
                    ) {
                        // decalre the counter variable count
                        var count = 0
                        // create a flag varaible
                        var flag = 0
                        // this block we have find the row height
                        // becuase we need to find the max column height then set that max column height as row height
                        // row height means we need to set the max height value to all the column height in the row
                        // process the column one by one
                        row.asJsonObject.entrySet().forEach { col ->
                            // check the i is zero means
                            // get the store the sno value

                            if (i == 0) {
                                first_col = col.value.toString().replace("\"", "").toInt()
                                first_col_name = col.key.toString().replace("\"", "")
                                // incrment the counter variable beacuse next time i don't want to execute this block
                                i++
                            }
                            // check the current column format is not record and image
                            if (tableconfig[count].format != "record" && tableconfig[count].format != "image" && tableconfig[count].format != "notes" && tableconfig[count].format != "url") {
                                // call the wrap content to find the max character in the text
                                var current_col_max_char = wrap_content(tableconfig[count].width.value)
                                // check the current colum value length is greater than max character value means
                                // wrap the content
                                if (col.value.toString().length >= current_col_max_char) {
                                    // change the flag as 1
                                    flag = 1
                                    // check the line count
                                    var line_count =
                                        find_max_lines(col.value.toString(), current_col_max_char)
                                    // create a new height
                                    var col_height = (line_count * 30).dp
                                    // check current column height is greater than height means
                                    // change the height value to current column height
                                    if (col_height > height!!) {
                                        height = col_height
                                    }
                                }
                            }
                            // check the flag is zero
                            if (flag == 0) {
                                /// set the default height to height variable
                                height = tableconfig[count].height
                            }
                            // increment the counter variable
                            count++
                        }
                        // re-initalize the counter variable as zero
                        count = 0
                        // process the row data column by column
                        row.asJsonObject.entrySet().forEach { col ->
                            // check the current column was not hidden
                            if (!tableconfig[count].hidden) {
                                // call the tablebody function to show the data in UI
                                Tablebody(
                                    text = col.value.toString().replace("\"", "").toString(),
                                    weight = tableconfig[count].width,
                                    height = height!!,
                                    tableconfig[count].format,
                                    first_col,
                                    col.key.toString().replace("\"", "").toString(),tableconfig[count].editable,tableconfig[count].formattype,
                                    first_col_name!!,
                                    onUpdateData
                                )
                            }
                            // increment the counter variable
                            count++
                        }
                    }
                    // increment the counter variable
                    i++

                }
                row_count++
            }
        }
    }



    //===  FUNCTION  ================================================================
//  NAME        :   writeHeader
//  PURPOSE     :   this function is call the header,column search and table body to show the jqgrid
//  PARAMETERS  :   List<ColumnModel>,JsonArray,Call back function,Call back function,Call back function,Call back function,Call back function,Call back function,Int,call back function
//  RETURNS     :   NULL
//  DESCRIPTION :   this function is call the header,column search and table body to show the jqgrid
//================================================================================
    @RequiresApi(Build.VERSION_CODES.O)

    @Composable
    fun writeHeader(
        tableconfig: List<ColumnModel>,
        tabledata: JsonArray,
        onSearchFilter: (MutableList<SearchFilter>) -> Unit,
        pagesize: Int,
        onUpdateData: (Int, String, String, String) -> Unit
    ){

        // create a column layout
        Column(
            Modifier
                .fillMaxSize()
                .padding(30.dp)
                .padding(bottom = 30.dp) // Adjust bottom padding (it is used to add the scroll while incresing the pagination size)
                .padding(horizontal = 8.dp) // Adjust horizontal padding
        ) {

            Spacer(modifier = Modifier.height(20.dp))
            // create a row layout
            Row(
                Modifier
                    .background(primary_color,shape = RoundedCornerShape(8.dp))
            ){
                // process the table configuration one by one row to show the table header
                tableconfig.forEach{ it ->
                    // check the current column is not hidden
                    if(!it.hidden){
                        Tableheader(text =it.label , weight =it.width, colname =it.name,it.index, onSearchFilter,height = it.height,it.sorttype,it.sortable,tabledata)
                    }
                }
            }
            // create a row layout
            Row{
                Modifier.background(secondary_color,shape = RoundedCornerShape(8.dp))
                // process the table configuration one by one to show the column search filter
                // each column
                tableconfig.forEach{ it ->
                    // println("IT :"+it)
                    var col_name=""
                    /*if(tablename.get(0) == "warranty" && it.name == "Update")
                        col_name="Update"
                    else if(tablename.get(0) == "warranty" && it.name == "remaning_days")
                        col_name="Remaning Days"
                    else
                        col_name=column_name[it.index]*/
                    //println("Search Filed : ${it.index} = ${searchFilters[it.index].svalue}")
                    Column_Search(
                        text =it.label , weight =it.width,it.index,it.name,tabledata, onSearchFilter,
                        height = it.height,searchFilters[it.index].svalue,it.hidden,it.sortable)
                }
            }
            // create a lazycolumn for vertical scroll bar
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(top = 5.dp)
                    .background(secondary_color, shape = RoundedCornerShape(8.dp))
            ){
                item{
                    // call the write column function to write the table body content
                    // writeColumn(tableconfig, tabledata,onUpdateData,Warranty_update,pagesize,imagePreview)
                    writeColumn(tableconfig, tabledata,pagesize,onUpdateData)
                }
            }
        }
    }

    //===  FUNCTION  ================================================================
//  NAME        :   update_table_config
//  PURPOSE     :   this function is used to just change the value of hidden in the table configuration
//  PARAMETERS  :   List<ColumnModel>,Int,Boolean,Boolean
//  RETURNS     :   NULL
//  DESCRIPTION :   this function is used to just change the value of hidden in the table configuration
//================================================================================
    fun update_table_config(tableconfig: List<ColumnModel>, index:Int, flag : Boolean,flag2: Boolean): List<ColumnModel> {
        // create a new table config
        var new_config = tableconfig
        // change the hidden value
        new_config[index].hidden=flag
        // change the hide column value
        new_config[index].hide_column=flag2
        // return the new config
        return new_config
    }



    //===  FUNCTION  ================================================================
//  NAME        :   TopBar
//  PURPOSE     :   this function contains the jqgrid paggination part
//  PARAMETERS  :   Modifer,call back function,call back function,JsonArray,call back function,Int,Int,Int,call back function,Array<Int>,Int,Call Back function
//  RETURNS     :   NULL
//  DESCRIPTION :   this function contains the jqgrid paggination part
//================================================================================

    @Composable
    fun TopBar(
        modifier: Modifier = Modifier,
        onNextPage: () -> Unit,
        onPrevPage: () -> Unit,
        tabledata: JsonArray,
        onUpdateConfig: (List<ColumnModel>, Int, Boolean,Boolean) -> Unit,
        pagesize: Int,
        total_rec: Int,
        offset: Int,
        onRowUpdate: (Int) -> Unit,
        pagination: Array<Int>,
        currentPage: Int,
        onAdavanceSearch: () -> Unit
    ) {
        // create a composable flags for column chooser and adavanced search filter dialog box showing
        var showColumnChooser by remember{ mutableStateOf(false) }
        var adance_search by remember{ mutableStateOf(false) }

        Surface(
            modifier = modifier
                .padding(vertical = 20.dp, horizontal = 8.dp)
                .height(30.dp),
            color=primary_color,

            ) {
            // create a column layout
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // create a row Layout
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ){
                    var column_name : List<String>?=null
                    var columns=""
                    tableconfig.forEach {
                        columns+="${it.label}.${it.name},"
                    }
                    columns=columns.removeSuffix(",")
                    column_name=columns.split(",")
                    Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically,
                        modifier =  Modifier
                            .background(secondary_color, shape = RoundedCornerShape(8.dp)) // Rounded corners
                            .height(25.dp) // Adjust height to fit content properly
                            .width(80.dp)
                            .padding(horizontal = 6.dp, vertical = 4.dp) // Reduce extra spacing){
                    ){
                        IconButton(modifier = Modifier.fillMaxSize(),onClick = {
                        }
                            // modifier = Modifier.padding(start = 6.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxSize()
                                .clickable {
                                    var count=0
                                    column_name.forEach {
                                        // println("Column :"+it)
                                        var c_index = column_name.indexOf(it)
                                        searchFilters[c_index].svalue=""
                                        searchFilters[c_index].operator=""
                                        var col = it.split(".").get(1)
                                        searchFilters[count] = SearchFilter(col,"","","","","")
                                        //println("Search Filter :"+searchFilters[count])
                                        count++
                                    }
                                    onAdavanceSearch()
                                }){
                                Icon(imageVector = Device_reset, contentDescription = "Filter Reset", modifier = modifier.size(24.dp), tint = primary_color)
                                Text("Reset", fontSize = 16.sp, color = primary_color)
                            }

                        }

                    }
                    Spacer(modifier=modifier.width(8.dp))
                    Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically,
                        modifier =  Modifier
                            .background(secondary_color, shape = RoundedCornerShape(8.dp)) // Rounded corners
                            .height(25.dp) // Adjust height to fit content properly
                            .width(160.dp)
                            .padding(horizontal = 6.dp, vertical = 4.dp) // Reduce extra spacing){
                    ){
                        // create a icon button search for advacned search
                        IconButton(onClick = {
                            // enable the advacned search filter dialoge box

                        },
                            modifier = Modifier.fillMaxSize()
                            //        .padding(start = 6.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxSize().clickable {
                                adance_search=true
                            }){
                                // search Icon
                                Icon(
                                    imageVector = Manage_search,
                                    contentDescription = "Search",
                                    modifier=modifier.size(24.dp),
                                    tint=primary_color
                                )
                                Text("Advanced Search", fontSize = 16.sp, color = primary_color)
                            }
                        }
                    }


                    // call the advacned search function when the adavanced search is enabled
                    advanced_search(adance_search,onClose={adance_search=false},column_name,onAdavanceSearch)
                    Spacer(modifier=modifier.width(8.dp))
                    // set the icon tableview for column chooser
                    /*IconButton(onClick = {showColumnChooser = true },modifier = Modifier.padding(start = 4.dp)) {
                        // table view icon
                        Icon(
                            imageVector = Table,
                            contentDescription = "Back",
                            modifier=modifier.size(24.dp),
                            tint=secondary_color
                        )
                    }*/
                    Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically,
                        modifier =  Modifier
                            .background(secondary_color, shape = RoundedCornerShape(8.dp)) // Rounded corners
                            .height(25.dp) // Adjust height to fit content properly
                            .width(150.dp)
                            .padding(horizontal = 6.dp, vertical = 4.dp) // Reduce extra spacing){
                    ){
                        // create a icon button search for advacned search
                        IconButton(onClick = {
                            // enable the advacned search filter dialoge box

                        },
                            modifier = Modifier.fillMaxSize()
                            //        .padding(start = 6.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxSize().clickable {
                                showColumnChooser = true
                            }){
                                // search Icon
                                Icon(
                                    imageVector = Table,
                                    contentDescription = "Back",
                                    modifier=modifier.size(24.dp),
                                    tint=primary_color
                                )
                                Text("Column Chooser", fontSize = 16.sp, color = primary_color)
                            }
                        }
                    }
                    // call the column chooser when the showcolumn chooser flag is enabled
                    columnchooser(visible = showColumnChooser, onClose = {showColumnChooser=false},tableconfig,onUpdateConfig)
                    ////println("back button Current Page :"+currentPage)
                    // icon button for preivous page
                    IconButton(onClick = {
                        if (currentPage > 1)
                            onPrevPage()
                    },
                        modifier = Modifier.padding(start = 8.dp),
                        enabled = (currentPage > 1)
                    ) {
                        // preivous page icon
                        Icon(
                            imageVector =  Keyboard_double_arrow_left,
                            //painter = painterResource(R.drawable.leftarrow),
                            contentDescription = "Back",
                            modifier=modifier.size(24.dp),
                            tint=if(currentPage>1) secondary_color else gray
                        )
                        //FaIcon(faIcon = FaIcons.FastBackward, tint = secondary_color)
                    }
                    Spacer(modifier=modifier.width(16.dp))
                    // set the text
                    Text(text = "Page",color=secondary_color)
                    Spacer(modifier=modifier.width(16.dp))
                    // show the current page value in text field with read only
                    BasicTextField(
                        value = currentPage.toString(),
                        onValueChange ={},
                        readOnly = true,
                        modifier = Modifier
                            .border(1.dp, secondary_color)
                            .height(20.dp)
                            .width(20.dp)
                            .background(secondary_color)
                            .wrapContentSize(align = Alignment.Center),
                        textStyle = TextStyle(color = black, textAlign = TextAlign.Center)

                    )
                    Spacer(modifier=modifier.width(16.dp))
                    // //println("Current page on Next button :"+currentPage)
                    // //println("Total records :${total_rec} Page size : ${pagesize}")
                    // show the next page icon button
                    IconButton(onClick = {
                        if (total_rec > pagesize){
                            onNextPage()
                        }
                    },modifier = Modifier.padding(start = 8.dp),
                        enabled = total_rec > pagesize) {
                        // next page icon
                        Icon(
                            imageVector = Keyboard_double_arrow_right,
                            //painter = painterResource(R.drawable.rightarrow),
                            contentDescription = "Next",
                            modifier=modifier.size(24.dp),
                            tint=if(total_rec > pagesize) secondary_color else gray
                        )
                        //FaIcon(faIcon = FaIcons.FastForward, tint = secondary_color)
                    }
                    Spacer(modifier=modifier.width(16.dp))
                    // call the list of rows for choose the page size
                    list_of_rows(pagination,onRowUpdate)
                    Spacer(modifier=modifier.width(16.dp))
                    // check the data is empty means show the text there is no records
                    if(tabledata.isEmpty){
                        Text(text= "There is No Records", fontSize = 14.sp,color=secondary_color)
                    }else{
                        // else show the text
                        Text(text= "View", fontSize = 14.sp,color=secondary_color)
                        Spacer(modifier=modifier.width(10.dp))
                        // calculate how many reocrds in the page
                        // show the count of start to end rows
                        var end  = 0
                        var start = offset+1
                        if(total_rec > pagesize) {
                            end = offset + total_rec - 1
                        }else{
                            end= offset+total_rec
                        }
                        Text(text = "$start - $end", fontSize = 14.sp,color=secondary_color)
                        Spacer(modifier=modifier.width(10.dp))
                        Text(text = "Too Many Records", fontSize = 14.sp,color=secondary_color)
                    }
                }
            }
        }


    }




    //===  FUNCTION  ================================================================
//  NAME        :   columnchooser
//  PURPOSE     :   this used to hide and unhide the columns in the table
//  PARAMETERS  :   Boolean,Call back functions,List<ColumnModel>,Call back function,Int,Boolean,Boolean
//  RETURNS     :   NULL
//  DESCRIPTION :   this used to hide and unhide the columns in the table
//================================================================================
    @Composable
    fun columnchooser(
        visible:Boolean,
        onClose : () -> Unit,
        tableconfig: List<ColumnModel>,onUpdateConfig: (List<ColumnModel>, Int, Boolean,Boolean) -> Unit
    ){
        // check the visibale flag is enable or not
        if(visible){
            // create a dialog to show the column chooser
            Dialog(properties = DialogProperties(usePlatformDefaultWidth = false),onDismissRequest = onClose) {
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(primary_color)
                        .width(640.dp)
                        .height(320.dp)
                ) {
                    // create a column layout
                    Column(
                        modifier = Modifier
                            .background(primary_color)
                            .fillMaxWidth()
                    ) {
                        // create a row layout
                        Row(
                            Modifier
                                .height(40.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Spacer(modifier = Modifier.width(20.dp))
                            // show the text
                            Text(text = "Column Chooser", color=secondary_color)

                            Spacer(modifier = Modifier.weight(1f))
                            // create a close button
                            Button(modifier = Modifier
                                .height(30.dp)
                                .width(100.dp)
                                ,onClick = onClose,colors= ButtonDefaults.buttonColors(secondary_color), shape = RoundedCornerShape(8.dp)

                            ){
                                Text(text="Close",color=primary_color)
                            }
                        }
                        // create a line divider
                        Divider(color = secondary_color, thickness = 1.dp)
                        // create a row layout
                        Row(
                            Modifier.height(40.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Spacer(modifier = Modifier.width(8.dp))
                            // show the text
                            Text("Hide Columns",color=secondary_color, modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp))
                            // create a line divider
                            Divider(color = secondary_color, thickness = 1.dp, modifier = Modifier.size(width = 2.dp, height = 40.dp))
                            //Divider(color = secondary_color, thickness = 1.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            // show the text
                            Text("Add Columns",color=secondary_color, modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp))
                        }
                        // create a line divider
                        Divider(color = secondary_color, thickness = 1.dp)
                        // create a row layout
                        Row(
                            horizontalArrangement = Arrangement.Center
                        ){
                            // create a column layout
                            Column{
                                // create lazycolumn for scolling
                                LazyColumn(
                                    modifier = Modifier
                                        .border(1.dp, secondary_color)
                                ){
                                    // set flag variable
                                    var hide_flag=0
                                    // prcoess the table config one by one
                                    item{
                                        tableconfig.forEach{
                                            // check the hidden is false or not
                                            if(!it.hidden && !it.hide_column){
                                                hide_flag=1
                                                // create a row layout
                                                Row(
                                                    modifier = Modifier
                                                        .height(40.dp)
                                                        .width(320.dp)
                                                        .padding(8.dp)
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(secondary_color),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ){
                                                    // add some spacer
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    // show column names
                                                    Text(text="${it.label}", color = primary_color)
                                                    // show the icon button delete
                                                    IconButton(
                                                        // when click the delete button we clear the filters
                                                        // on the column then hide the column
                                                        onClick = {
                                                            searchFilters[it.index].svalue=""
                                                            searchFilters[it.index].sorttype=""
                                                            searchFilters[it.index].sort=""
                                                            searchFilters[it.index].sortmethod=""
                                                            searchFilters[it.index].operator=""
                                                            onUpdateConfig(tableconfig,it.index,!it.hidden,it.hide_column)
                                                        },
                                                        modifier = Modifier.padding(start = 8.dp))
                                                    {
                                                        // delete icon
                                                        Icon(
                                                            imageVector = Delete_sweep,
                                                            //painter = painterResource(R.drawable.leftarrow),
                                                            contentDescription = "Delete",
                                                            modifier=Modifier.size(24.dp),
                                                            tint= primary_color
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        // check the hide flag is zero means
                                        // show the text No columns in the columnchooser
                                        //item{
                                        if(hide_flag == 0){
                                            // create a row layout
                                            Row(
                                                modifier = Modifier
                                                    .height(40.dp)
                                                    .width(320.dp)
                                                    .padding(8.dp)
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(secondary_color),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center
                                            ){
                                                // Spacer(modifier = Modifier.width(4.dp))
                                                Text("No Columns",color= primary_color, modifier = Modifier.padding(start = 8.dp))
                                            }
                                        }
                                        //}

                                    }

                                }

                            }
                            // create a column layout
                            Column{
                                // create a lazy column for scrolling
                                LazyColumn(
                                    modifier = Modifier
                                        .border(1.dp, secondary_color)
                                ){
                                    var hide_flag=0
                                    // process the table config one by one
                                    item{
                                        tableconfig.forEach {
                                            // check the hidden value is true or not
                                            if(it.hidden && !(it.hide_column)){
                                                hide_flag=1
                                                Row(
                                                    modifier = Modifier
                                                        .height(40.dp)
                                                        .width(320.dp)
                                                        .padding(8.dp)
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(secondary_color),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ){
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    // show column name
                                                    Text(text="${it.label}",color= primary_color)
                                                    // create add icon button
                                                    // to show the column in the table
                                                    IconButton(
                                                        // when user click the table just show column in the table
                                                        onClick = {
                                                            onUpdateConfig(tableconfig,it.index,!it.hidden,it.hide_column)
                                                        },
                                                        modifier = Modifier.padding(start = 8.dp))
                                                    {
                                                        // add icon
                                                        Icon(
                                                            imageVector = Add_to_photos,
                                                            contentDescription = "Add",
                                                            modifier=Modifier.size(24.dp),
                                                            tint= primary_color
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        // check the hide flag is zero means
                                        // show the text No columns in the columnchooser
                                        if(hide_flag == 0){
                                            // create a row layout
                                            Row(
                                                modifier = Modifier
                                                    .height(40.dp)
                                                    .width(320.dp)
                                                    .padding(8.dp)
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(secondary_color),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center
                                            ){
                                                // Spacer(modifier = Modifier.width(4.dp))
                                                Text("No Columns",color= primary_color, modifier = Modifier.padding(start = 8.dp))
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    //===  FUNCTION  ================================================================
//  NAME        :   list_of_rows
//  PURPOSE     :   this is used to choose the how many data will be show in the pagelist_of_rows
//  PARAMETERS  :   Array<Int>,call back function
//  RETURNS     :   NULL
//  DESCRIPTION :   this is used to choose the how many data will be show in the pagelist_of_rows
//================================================================================
    @Composable
    fun list_of_rows(pagination: Array<Int>, onRowUpdate: (Int) -> Unit) {
        // declare the recomposable flags
        var selectedoption by remember {
            mutableStateOf(pagination[0].toString())
        }
        var showDialog by remember {
            mutableStateOf(false)
        }
        // check show dialog flag is enabled or not
        if(showDialog){
            // create a dialog box
            Dialog(properties = DialogProperties(usePlatformDefaultWidth = false),onDismissRequest = {showDialog=false}) {
                Surface(
                    modifier = Modifier
                        .background(primary_color)
                        .width(640.dp),
                    shape = MaterialTheme.shapes.medium
                ){
                    // create a column layout
                    Column(modifier = Modifier.background(primary_color)){
                        // create lazycolumn for scroll event
                        LazyColumn {
                            // show the text List of Rows
                            item{
                                Column(modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp)
                                    .border(1.dp, color = primary_color)
                                    .background(secondary_color), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
                                    Text("List Of Rows", color = primary_color)

                                }
                                // write line divider
                                Divider(color = secondary_color, thickness = 2.dp)

                            }
                            // process the array of pagination data one by one
                            items(pagination){
                                // create a column layout with clickable event
                                Column(modifier = Modifier
                                    .clickable {
                                        // when user click the column
                                        // get the text and store it
                                        // then update table data for show selected pagination data
                                        selectedoption = it.toString()
                                        onRowUpdate(it)
                                        // disable the flag
                                        showDialog = false
                                    }
                                    .padding(16.dp)
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .border(
                                        1.dp,
                                        // change the color of column
                                        // to difference the selected and unselected columns showing
                                        color = if (it.toString() == selectedoption) primary_color else secondary_color
                                    )
                                    // also doing the same above process for background color
                                    .background(if (it.toString() == selectedoption) secondary_color else primary_color)
                                    , verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                                    // show the pagination data
                                    Text(text = it.toString(),color = if(selectedoption == it.toString()) primary_color else secondary_color)
                                }
                            }
                        }
                    }
                }
            }
        }
        // create a column layout with clickable event
        Column(modifier = Modifier
            .clickable {
                showDialog = true
            }
            .background(secondary_color)
            .border(1.dp, primary_color)
            .width(60.dp)) {
            // create a row layout
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier
                .background(secondary_color)
                .border(1.dp, primary_color)
                .width(60.dp)){
                // show the selected text
                Text(selectedoption, color = black)
                Spacer(modifier = Modifier.width(16.dp))
                //FaIcon(faIcon = FaIcons.AngleDown, tint = black)
                Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "Down Arrow")
            }
        }
    }




    //===  FUNCTION  ================================================================
//  NAME        :   advanced_search
//  PURPOSE     :   this the main function is used to show the adavanced search operation
//  PARAMETERS  :   Boolean,Call back funtions,List<String>,Call back functions
//  RETURNS     :   NULL
//  DESCRIPTION :   this the main function is used to show the adavanced search operation
//================================================================================
    @Composable
    fun advanced_search(
        visible: Boolean,
        onClose: () -> Unit,
        column_name: List<String>,
        onAdavanceSearch: () -> Unit
    ){
        // check the flag is enabled or not
        if(visible){
            // create a recomposable variables
            var textState by remember { mutableStateOf("") }
            var column_select by remember { mutableStateOf("") }
            var operator_select by remember { mutableStateOf("") }
            var svalue by remember { mutableStateOf("") }
            var coldatatype by remember { mutableStateOf("") }
            // store the operator values
            var params by remember { mutableStateOf("contains,not contains,begins with,ends with,eqaul to,not equal to,less than,less than or equal to,greater than,greater than or eqaul to") }

            // create a dialog box
            Dialog(properties = DialogProperties(usePlatformDefaultWidth = false),onDismissRequest = onClose) {
                // use the full sapce in the dialog box
                Surface(
                    modifier = Modifier
                        .background(primary_color)
                        .width(640.dp)
                        .height(160.dp),
                    shape = MaterialTheme.shapes.medium
                ){
                    // create a column layout
                    Column(modifier = Modifier.background(primary_color))
                    {
                        // create a Row layout
                        Row(
                            Modifier
                                .height(50.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Spacer(modifier = Modifier.width(10.dp))
                            Row(horizontalArrangement = Arrangement.Start){
                                // show the text
                                Text("Advanced Search",color=secondary_color)
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            /*Row(horizontalArrangement = Arrangement.End){
                                // show the close button
                                // once user click the button apply the selected filters in the jqgrid table
                                Button(onClick = {
                                    var count=0
                                    column_name.forEach {
                                        println("Column :"+it)
                                        var c_index = column_name.indexOf(it)
                                        searchFilters[c_index].svalue=""
                                        searchFilters[c_index].operator=""
                                        println("Column Select :"+column_select)
                                        var col = it.split(".").get(1)
                                        searchFilters[count] = SearchFilter(col,svalue,"","","","")
                                        println("Search Filter :"+searchFilters[count])
                                        count++
                                    }
                                    onAdavanceSearch()
                                    onClose()
                                },Modifier.height(30.dp), colors= ButtonDefaults.buttonColors(secondary_color), shape = RoundedCornerShape(8.dp)){
                                    // set the button text
                                    Text(text="Reset",color=primary_color)
                                }
                            }*/
                            Spacer(modifier = Modifier.width(20.dp))
                            Row(horizontalArrangement = Arrangement.End){
                                // show the close button
                                // once user click the button apply the selected filters in the jqgrid table
                                /*Button(onClick = {
                                    onClose()
                                },Modifier.height(30.dp), colors= ButtonDefaults.buttonColors(secondary_color), shape = RoundedCornerShape(8.dp)){
                                    // set the button text
                                    Text(text="Close",color=primary_color)
                                }*/
                                IconButton(onClick = { onClose() }) {
                                    Icon(imageVector = Cancel, contentDescription = "Cancel", tint = secondary_color, modifier = Modifier.size(32.dp))
                                }
                            }
                        }
                        // add divider
                        Divider(color = secondary_color, thickness = 1.dp)
                        // create a row layout
                        Row (
                            modifier = Modifier.fillMaxSize()
                        ){
                            var columnname= ""
                            tableconfig.forEach {
                                columnname+="${it.name}.${it.label},"
                            }
                            columnname=columnname.removeSuffix(",")

                            // call the drop down menu to show the column names
                            drop_down_menu(options = columnname.split(","),change_flag=
                            {
                                var count=0
                                tableconfig.forEach { col ->
                                    // println("Column :${col.label} === selected Col : ${it}" )
                                    if(col.label == it.split(".").get(1)) {
                                        println("Selected Col :${column_name[count]}")
                                        column_select = column_name[count]
                                        println("Selected Col :$column_select")
                                    }
                                    count++
                                }
                            },"Columns")
                            tableconfig.forEach {

                                if(column_select!="") {

                                    // check the selected column
                                    // then give the selected column sorrtype to get_option function to get the filter options
                                    //if (it.name == column_select) {
                                    if(it.name == column_select.split(".").get(1)){
                                        coldatatype=it.sorttype
                                        params = get_options(it.sorttype)
                                        //column_select=it.name
                                    }
                                }
                            }
                            // call the drop down function to show the operators
                            drop_down_menu(options = params.split(","), change_flag =
                            {
                                println("Operator :"+it)
                                println("Column Select :"+column_select)
                                // check the column is selected or not
                                if(column_select!="")
                                    operator_select=it
                            },"Operators")
                            if(!(column_select.isNullOrBlank()) && !(column_select.isNullOrEmpty())){

                                var paramlist = params.split(",")
                                if(!(operator_select in paramlist)){
                                    operator_select=""
                                }
                            }
                            // get the selected operator
                            var operator = get_operator(operator_select)

                            // get the colum index
                            var col_index = column_name.indexOf(column_select)


                            // create a column layout
                            Column(modifier = Modifier.padding(15.dp), verticalArrangement = Arrangement.Center){
                                // show the text filed for search the values
                                BasicTextField(
                                    value = textState,
                                    onValueChange ={newvalue ->
                                        textState=newvalue
                                        svalue=newvalue
                                    },
                                    modifier = Modifier
                                        .border(1.dp, secondary_color, shape = RoundedCornerShape(8.dp))
                                        .height(50.dp)
                                        .width(160.dp)
                                        .padding(16.dp),
                                    textStyle = TextStyle(color = secondary_color),
                                    keyboardOptions =
                                    if(coldatatype.isNullOrEmpty() || coldatatype.isNullOrBlank() || coldatatype == "number" || coldatatype=="float") KeyboardOptions(keyboardType = KeyboardType.Number).copy(imeAction = ImeAction.Done)
                                    else KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
                                )
                            }
                            // create a column layout
                            Column(modifier = Modifier.padding(15.dp), verticalArrangement = Arrangement.Center){
                                var notification_flag by remember {
                                    mutableStateOf(false)
                                }
                                var error_flag by remember {
                                    mutableStateOf(false)
                                }

                                if(notification_flag){
                                    // show the notification
                                    InformativeFancyDialog(
                                        title = "Warranty Vault",
                                        message = "The Operator you have selected is not valid for this column.Please select the appropriate operator.else both column and opertor field is empty",
                                        dismissTouchOutside = { notification_flag=false },
                                        neutralButtonClick = {notification_flag=false},
                                        // set the action for dialog was information
                                        dialogActionType = DialogActionType.INFORMATIVE
                                    )
                                }
                                if(error_flag){
                                    // show the error dialog
                                    ErrorFancyDialog(
                                        // set the title
                                        title = "Error",
                                        // set the error message
                                        message = "The column you selected is numeric, but the value you entered contains alphabetic characters. Please provide a numeric value.",
                                        // set the action for outside clicking
                                        dismissTouchOutside = { error_flag = false },
                                        // set the dialog type
                                        dialogActionType = DialogActionType.INFORMATIVE,
                                        // set the action for clicking the ok button
                                        neutralButtonClick = {error_flag=false}
                                    )
                                }
                                // create a button
                                // once user click the button apply the selected filters in the jqgrid table
                                /* Button(onClick = {
                                     println("OPERATOR :"+operator)
 
                                     if(column_select!="" && operator_select!="" && svalue != ""){
 
                                         if(operator == "Invalid Operator"){
                                             notification_flag=true
                                         }
                                         else if((coldatatype == "number" || coldatatype=="float") && (Pattern.matches("[a-zA-Z]+", svalue)))
                                         {
                                             error_flag=true
                                         }
                                         else{
                                             column_name.forEach {
                                                 println("Index :"+column_name.indexOf(it))
                                                 var c_index = column_name.indexOf(it)
                                                 searchFilters[c_index].svalue=""
                                                 searchFilters[c_index].operator=""
                                             }
                                             var column_s=column_select.split(".").get(1)
                                             println("Column Select : ${column_s} ,svalue : $svalue , operator  :$operator")
                                             searchFilters[col_index] = SearchFilter(column_s,svalue,operator,"","","")
                                             // //println("SELCTED VALUE :"+searchFilters[col_index])
                                             onAdavanceSearch()
                                             onClose()
                                         }
                                     }
                                     else if(operator_select.isNullOrBlank() || operator_select.isNullOrEmpty()){
                                         println("SELCET :"+operator_select)
                                         notification_flag=true
                                     }
                                 }, colors= ButtonDefaults.buttonColors(secondary_color), shape = RoundedCornerShape(8.dp)){
                                     // set the button text
                                     Text(text="Ok",color=primary_color)
                                 }*/

                                IconButton(onClick = {
                                    println("OPERATOR :"+operator)

                                    if(column_select!="" && operator_select!="" && svalue != ""){

                                        if(operator == "Invalid Operator"){
                                            notification_flag=true
                                        }
                                        else if((coldatatype == "number" || coldatatype=="float") && (Pattern.matches("[a-zA-Z]+", svalue)))
                                        {
                                            error_flag=true
                                        }
                                        else{
                                            column_name.forEach {
                                                println("Index :"+column_name.indexOf(it))
                                                var c_index = column_name.indexOf(it)
                                                searchFilters[c_index].svalue=""
                                                searchFilters[c_index].operator=""
                                            }
                                            var column_s=column_select.split(".").get(1)
                                            println("Column Select : ${column_s} ,svalue : $svalue , operator  :$operator")
                                            searchFilters[col_index] = SearchFilter(column_s,svalue,operator,"","","")
                                            // //println("SELCTED VALUE :"+searchFilters[col_index])
                                            onAdavanceSearch()
                                            onClose()
                                        }
                                    }
                                    else if(operator_select.isNullOrBlank() || operator_select.isNullOrEmpty()){
                                        println("SELCET :"+operator_select)
                                        notification_flag=true
                                    }

                                }) {
                                    Icon(imageVector = Enter, contentDescription = "Cancel", tint = secondary_color, modifier = Modifier.size(32.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    //===  FUNCTION  ================================================================
//  NAME        :   get_options
//  PURPOSE     :   this function is used to get the operators for the given data type
//  PARAMETERS  :   String
//  RETURNS     :   String
//  DESCRIPTION :   this function is used to get the operators for the given data type
//================================================================================

    fun get_options(data_type:String) : String{
        println("DataType : ${data_type}")
        // check the data type using when condition then return the opertors list as string
        var result : String=when(data_type){
            //var params="contains,not contains,begins with,ends with,eqaul to, not equal to, less than,less than or equal to,greater than,greater than or eqaul to"
            "number" -> "contains,not contains,begins with,ends with,eqaul to,not equal to,less than,less than or equal to,greater than,greater than or eqaul to"
            "string" ->"contains,not contains,begins with,ends with,eqaul to,not equal to"
            "float" ->"contains,not contains,begins with,ends with,eqaul to,not equal to,less than,less than or equal to,greater than,greater than or eqaul to"
            "date" -> "contains,not contains,begins with,ends with,eqaul to,not equal to,less than,less than or equal to,greater than,greater than or eqaul to"
            else -> "Invalid Data Type"
        }
        return  result
    }


    //===  FUNCTION  ================================================================
//  NAME        :   get_operator
//  PURPOSE     :   this function is used to get the operator for the given opertor string
//  PARAMETERS  :   String
//  RETURNS     :   String
//  DESCRIPTION :   this function is used to get the operator for the given opertor string
//================================================================================
    fun get_operator(op:String):String{
        // using the when condtion to return the operator name as string
        // check selected operator and return the select operator we used value to fetch data in database
        if(op == "contains")
            return "cn"
        else if(op == "not contains")
            return "nc"
        else if(op == "begins with")
            return "bw"
        else if(op == "ends with")
            return "ew"
        else if(op == "eqaul to")
            return "eq"
        else if(op == "not equal to")
            return "ne"
        else if(op == "less than")
            return "lt"
        else if(op == "less than or equal to")
            return "le"
        else if(op == "greater than")
            return "gt"
        else if(op == "greater than or eqaul to")
            return "ge"
        return  "Invalid Operator"
    }

    //===  FUNCTION  ================================================================
//  NAME        :   drop_down_menu
//  PURPOSE     :   this function is used to show the drop down box
//  PARAMETERS  :   List<String>,call back function,String
//  RETURNS     :   NULL
//  DESCRIPTION :   this function is used to show the drop down box
//================================================================================

    @Composable
    fun drop_down_menu(options: List<String>,change_flag: (String) -> Unit,Label:String) {
        // Declaring a boolean value to store
        // the expanded state of the Text Field
        var mExpanded by remember { mutableStateOf(false) }


        // Create a string value to store the selected text
        var mSelectedText by remember { mutableStateOf("Select") }

        var mTextFieldSize by remember { mutableStateOf(Size.Zero)}

        // Up Icon when expanded and down icon when collapsed
        val icon = if (mExpanded)
            Icons.Filled.KeyboardArrowUp
        else
            Icons.Filled.KeyboardArrowDown

        Column(modifier = Modifier.padding(5.dp)) {

            // Create an Outlined Text Field
            // with icon and not expanded
            OutlinedTextField(
                value = mSelectedText,
                onValueChange = { mSelectedText = it },
                modifier = Modifier
                    .height(60.dp)
                    .width(160.dp)
                    .onGloballyPositioned { coordinates ->
                        // This value is used to assign to
                        // the DropDown the same width
                        mTextFieldSize = coordinates.size.toSize()
                    },
                readOnly = true,
                singleLine = true,
                label = {Text("${Label}",color=secondary_color)},
                trailingIcon = {
                    Icon(icon,"contentDescription",
                        Modifier.clickable { mExpanded = !mExpanded }, tint = secondary_color)
                },
                textStyle = TextStyle(color= secondary_color)
            )

            // Create a drop-down menu with list of cities,
            // when clicked, set the Text Field text as the city selected
            DropdownMenu(
                expanded = mExpanded,
                onDismissRequest = { mExpanded = false },
                modifier = Modifier
                    .width(with(LocalDensity.current) { mTextFieldSize.width.toDp() })
                    .background(primary_color)
                    .heightIn(max = 400.dp) // Limit dropdown height to prevent cutting
                // .verticalScroll(rememberScrollState()) // Make it scrollable
            ) {
                options.forEach { label ->
                    var selected = if(label.contains(".")) label.split(".").get(1) else label
                    DropdownMenuItem(text =
                    {

                        Text(text = selected,color=secondary_color)
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            //.fillMaxHeight()
                            //.height(100.dp)
                            .clip(shape = RoundedCornerShape(8.dp))
                            .border(
                                1.dp,
                                if (mSelectedText == selected) primary_color else secondary_color,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .background(
                                if (mSelectedText == selected)
                                    secondary_color
                                else
                                    primary_color
                            )
                            .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center){
                            // show the drop down option text
                            Text(selected, fontSize = 14.sp,color=
                            if(mSelectedText == selected)
                                black
                            else
                                secondary_color
                            )
                        }
                    } , onClick = {
                        mSelectedText = selected
                        mExpanded = false
                        //println("SELCTED VALUE :"+label)
                        change_flag(label)
                    })
                }
            }
        }
    }




}
