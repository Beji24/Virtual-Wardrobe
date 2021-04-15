package com.example.virtualdrawerv2.DBhelper

import android.content.ClipData.Item
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteQueryBuilder
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper
import java.sql.SQLException


class DBHelper(context: Context): SQLiteAssetHelper(context, DB_NAME, null, DB_VER)
{
    companion object{
        private val DB_NAME = "SaveBitMap.db"
        private val DB_VER = 1

        private val TBL_NAME = "VirtualDrawer"
        private val COL_CATEGORY = "Category"
        private val COL_DATA = "Data"

    }

    @Throws(SQLException::class)
    fun addBitmap(category: String, image: ByteArray)
    {
        val database :SQLiteDatabase  = this.writableDatabase
        val cv = ContentValues()

        cv.put(COL_CATEGORY, category)
        cv.put(COL_DATA, image)
        database.insert(TBL_NAME, null, cv)

    }

    fun getBitmapByNameDesc(category: String):ByteArray?
    {
        val db = this.writableDatabase
        val qb = SQLiteQueryBuilder()

        val sqlSelect = arrayOf(COL_DATA)

        qb.tables = TBL_NAME
        val c = qb.query(db, sqlSelect, "Category = ?", arrayOf(category), null, null, "ID DESC", "1")

        var result:ByteArray?=null
        if(c.moveToFirst())
        {
            do{
                result = c.getBlob(c.getColumnIndex(COL_DATA))
            }while (c.moveToNext())
        }

        return result

    }

    fun getBitmapByNameAndID(category: String, index: Int):ByteArray?
    {
        val db = this.writableDatabase
        val qb = SQLiteQueryBuilder()

        val sqlSelect = arrayOf(COL_DATA)

        qb.tables = TBL_NAME
        val args = arrayOf(index.toString(), category)

        val c = qb.query(db, sqlSelect, "ID = ? AND Category = ?", args, null, null, null)

        var result:ByteArray?=null
        if(c.moveToFirst())
        {
            do{
                result = c.getBlob(c.getColumnIndex(COL_DATA))
            }while (c.moveToNext())
        }

        return result

    }

    fun getLastID(category: String):Int?
    {
        val db = this.writableDatabase
        val qb = SQLiteQueryBuilder()

        val sqlSelect = arrayOf("ID")

        qb.tables = TBL_NAME
        val c = qb.query(db, sqlSelect, "Category = ?", arrayOf(category), null, null, "ID DESC","1")

        var result:Int?=null
        if(c.moveToFirst())
        {
            do{
                result = c.getInt(c.getColumnIndex("ID"))
            }while (c.moveToNext())
        }

        return result

    }

    fun getTotalImages(category: String):Int?
    {
        val db = this.writableDatabase
        val qb = SQLiteQueryBuilder()

        val sqlSelect = arrayOf("COUNT(ID)")

        qb.tables = TBL_NAME
        val c = qb.query(db, sqlSelect, "Category = ?", arrayOf(category), null, null, null)

        var result:Int?=null
        if(c.moveToFirst())
        {
            do{
                result = c.getInt(c.getColumnIndex("COUNT(ID)"))
            }while (c.moveToNext())
        }

        return result

    }


    fun deleteImage(index: Int) {
        val db = this.writableDatabase
        val whereClause = "ID=?"
        val whereArgs = arrayOf(index.toString())
        db.delete(TBL_NAME, whereClause, whereArgs)
    }
}