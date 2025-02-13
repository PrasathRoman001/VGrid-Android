package com.vgrid_android.viewmodel
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.vgrid_android.view.SearchFilter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Locale

data class Date(val year: Int, val month: Int, val day: Int)
class TableViewModel : ViewModel() {

    private val _tableData =  MutableStateFlow<JsonArray?>(JsonArray())
    val tableData: StateFlow<JsonArray?> = _tableData


    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadJsonFromFile(filePath: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                delay(500)
                // println("File path:"+filePath)
                val file = File(filePath)
                if (file.exists()) {
                    val jsonString = file.readText()
                    val jsonArray = JsonParser.parseString(jsonString).asJsonArray
                    _tableData.value = jsonArray
                } else {
                    println("TableViewModel , File does not exist at path: $filePath")
                }
            } catch (e: Exception) {
                println("TableViewModel ,Error reading JSON file: ${e.message}")
            }
            finally {
                _isLoading.value = false  // Stop loading
            }
            //println("JSON data :"+tableData.value)
        }
    }


    fun updateJsonValue(
        fileName: String,
        sno: Int,
        columnName: String,
        newValue: Any,
        row_id: String,) {
        val file = File(fileName)
        if (!file.exists()) {
            println("File not found!")
            return
        }

        // Read JSON file
        val jsonString = FileReader(file).use { it.readText() }

        // Parse JSON into a list of maps (dynamic objects)
        val gson = Gson()
        val type = object : TypeToken<MutableList<MutableMap<String, String>>>() {}.type
        val dataList: MutableList<MutableMap<String, String>> = gson.fromJson(jsonString, type)

        // Find and update the entry
        val item = dataList.find {
            val snoValue = it["$row_id"]?.toInt()
            //println(" IT ------> ${snoValue}")
            snoValue == sno
        }
        // println("ITEM :"+item)
        if (item != null && item.containsKey(columnName)) {
            item[columnName] = newValue.toString()  // Update the specified column

            // Write updated JSON back to file
            FileWriter(file).use { writer ->
                gson.toJson(dataList, writer)
            }
            println("Updated $columnName successfully for sno $sno!")
        } else {
            println("Invalid sno or column name.")
        }
        val jString = file.readText()
        val jsonArray = JsonParser.parseString(jString).asJsonArray
        _tableData.value = jsonArray
    }

    fun Search_Data(
        fileName: String,
        searchFilters: MutableList<SearchFilter>,
        offset: Int,
        pagesize: Int
    ){
        viewModelScope.launch {

            _isLoading.value=true

            try {
                var itemList = readJsonFromFile(fileName)
                var search_data = filterJsonArray(itemList, searchFilters)
                var new_data = sortJsonArray(search_data, searchFilters)
                var paginateJsonArray = paginateJsonArray(new_data!!, offset, pagesize)
                _tableData.value = paginateJsonArray
            } catch (e: Exception) {
                println("TableViewModel ,Error reading JSON file: ${e.message}")
            }finally {
               // delay(200)
                _isLoading.value=false
            }
        }
    }


    fun sortJsonArray(jsonArray: JsonArray, patt: MutableList<SearchFilter>): JsonArray? {
       // println("Enter to the fiter function...!")

        var filter_data=Gson().fromJson(jsonArray.toString(),JsonArray::class.java)
        var sort_data: List<JsonElement>? = null
        //println("PATTERN :"+patt)
        patt.forEach { sort ->
            var colname = sort.colname
            var sort_type = sort.sort
            var flag=0
            if(sort_type.isNotEmpty()){
                var dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
                var sort_data_type = sort.sortmethod
                println("Sort Data type :"+sort_data_type)
                var comparator : Comparator<JsonElement> = when(sort_data_type){
                    "number" ->{
                        flag=0
                        //println("COL NAME :"+colname)
                        if(sort_type == "desc"){
                            Comparator{ a:JsonElement,b:JsonElement ->
                                //println("A Element:"+a)
                              //  println("B Element :"+b)

                                b.asJsonObject[colname].asInt.compareTo(a.asJsonObject[colname].asInt)
                            }
                        }else{
                            Comparator{ a:JsonElement,b:JsonElement ->
                                a.asJsonObject[colname].asInt.compareTo(b.asJsonObject[colname].asInt)
                            }
                        }
                    }
                    "string" ->{
                        flag=0
                        if(sort_type == "desc"){
                            Comparator{ a:JsonElement,b:JsonElement ->
                                b.asJsonObject[colname].asString.compareTo(a.asJsonObject[colname].asString,ignoreCase = true)
                            }
                        }else{
                            Comparator{ a:JsonElement,b:JsonElement ->
                                a.asJsonObject[colname].asString.compareTo(b.asJsonObject[colname].asString,ignoreCase = true)
                            }
                        }
                    }
                    "float" ->{
                        flag=0
                        if(sort_type == "desc"){
                            Comparator{ a:JsonElement,b:JsonElement ->
                                b.asJsonObject[colname].asFloat.compareTo(a.asJsonObject[colname].asFloat)
                            }
                        }else{
                            Comparator{ a:JsonElement,b:JsonElement ->
                                a.asJsonObject[colname].asFloat.compareTo(b.asJsonObject[colname].asFloat)
                            }
                        }
                    }
                    "date" -> {
                        //flag=1
                        if(sort_type == "desc"){
                            flag=2
                        }else{
                            flag=1
                        }
                        Comparator{ obj1,obj2 ->
                            var datefeild1 = obj1.asJsonObject[colname].asString
                            var datefeild2 = obj2.asJsonObject[colname].asString
                            var date1 = dateFormat.parse(datefeild1)
                            var date2 = dateFormat.parse(datefeild2)
                            date1.compareTo(date2)
                        }

                    }
                    else -> Comparator{ _,_-> 0}
                }
                if(flag==2){
                    sort_data = filter_data.sortedWith(comparator.reversed())
                }else {
                    sort_data = filter_data.sortedWith(comparator)
                }
            }
        }
        if(sort_data!=null){
            filter_data=Gson().fromJson(sort_data.toString(),JsonArray::class.java)
            //println("Sortded Data :"+sdata)
        }
        return filter_data
    }

    fun filterJsonArray(jsonArray: JsonArray, searchFilters: MutableList<SearchFilter>): JsonArray {

        val isNoFilterApplied = searchFilters.all { it.operator.isBlank() && it.svalue.isBlank() }

        // If no valid filters are applied, return the original JSON array
        if (isNoFilterApplied) {
            return jsonArray
        }

        val filteredList = jsonArray.filter { jsonElement ->
            val jsonObject = jsonElement.asJsonObject
            searchFilters.all { filter ->
                if (jsonObject.has(filter.colname)) {
                    val fieldValue = jsonObject[filter.colname].asString
                    //println("Filter :"+filter.operator)
                    when (filter.operator) {
                        "contains", "cn" -> fieldValue.contains(filter.svalue, ignoreCase = true)
                        "not_contains", "nc" -> !fieldValue.contains(filter.svalue, ignoreCase = true)
                        "begins_with", "bw" -> fieldValue.startsWith(filter.svalue, ignoreCase = true)
                        "ends_with", "ew" -> fieldValue.endsWith(filter.svalue, ignoreCase = true)
                        "equals", "eq" -> fieldValue == filter.svalue
                        "not_equals", "ne" -> fieldValue != filter.svalue
                        "greater_than", "gt" -> fieldValue.toDoubleOrNull()?.let { it > filter.svalue.toDouble() } ?: false
                        "greater_than_or_equal_to", "ge" -> fieldValue.toDoubleOrNull()?.let { it >= filter.svalue.toDouble() } ?: false
                        "less_than", "lt" -> fieldValue.toDoubleOrNull()?.let { it < filter.svalue.toDouble() } ?: false
                        "less_than_or_equal_to", "le" -> fieldValue.toDoubleOrNull()?.let { it <= filter.svalue.toDouble() } ?: false
                        else -> true // If the operator is unknown, do not filter
                    }
                } else {
                    false // If the field does not exist, exclude this entry
                }
            }
        }

        //println("Filter List :"+filteredList)
        var sampledata = JsonArray().apply {
            filteredList.forEach { add(it) }
        }
        println("Sample Data :"+sampledata)

        return JsonArray().apply {
            filteredList.forEach { add(it) }
        }
    }


    // Function to Read JSON File from Assets
    fun readJsonFromFile(fileName: String): JsonArray {
        val file = File(fileName)
        var itemList  = JsonArray()
        if (file.exists()) {
            val jsonString = file.readText()
            itemList = JsonParser.parseString(jsonString).asJsonArray
            return itemList
        } else {
            println("TableViewModel , File does not exist at path: $fileName")
        }
        return itemList
    }


    fun paginateJsonArray(jsonArray: JsonArray, offset: Int, limit: Int): JsonArray {
        // Ensure offset and limit are within bounds
        if (offset >= jsonArray.size() || limit <= 0) return JsonArray()

        // Extract the required sublist
        val paginatedList = jsonArray.drop(offset).take(limit)

        // Convert back to JsonArray
        return JsonArray().apply {
            paginatedList.forEach { add(it) }
        }
    }

}