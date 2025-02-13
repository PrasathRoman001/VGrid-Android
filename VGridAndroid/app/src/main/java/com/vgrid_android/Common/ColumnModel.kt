package com.vgrid_android.Common
import androidx.compose.ui.unit.Dp

// This is a bottom navigation item model.
data class ColumnModel(
    // declear Index var
    var index:Int,
    // store the label name.
    var label : String,
    // store the route name.
    var name : String,
    // declear Hidden var.
    var hidden: Boolean,
    // set width
    var width: Dp,
    // set sorting type
    var sorttype: String,
    // set height
    var height: Dp,
    // sorting or not set a boolean.
    var sortable: Boolean,
    // editable or not. set boolean.
    var editable:Boolean,
    // format th string.
    var format : String,
    // set the column name should be hide in column chooser
    var hide_column : Boolean,
    var formattype : String
)