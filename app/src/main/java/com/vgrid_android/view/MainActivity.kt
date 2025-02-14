package com.vgrid_android.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vgrid_android.Common.ColumnModel
import com.vgrid_android.ui.theme.VGridAndroidTheme
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalStdlibApi::class)
    @SuppressLint("ContextCastToActivity")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VGridAndroidTheme {
                val musicDir = application.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
                var copiedFilePath = File(musicDir,"sample.json").toString()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
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
                }
            }
        }
    }

}



