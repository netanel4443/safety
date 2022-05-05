package com.e.security

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.e.security.application.BaseApplication
import com.e.security.data.PlaceGeneralDetails
import com.e.security.data.writetoword.WriteToWord
import com.e.security.databinding.FragmentRecyclerviewAddBtnBinding
import com.e.security.di.components.MainActivityComponent
import com.e.security.sensors.CameraOperations
import com.e.security.ui.MainViewModel
import com.e.security.ui.activities.BaseActivity
import com.e.security.ui.dialogs.StudyPlaceInfoDialog
import com.e.security.ui.fragments.StudyPlaceReportsFragment
import com.e.security.ui.recyclerviews.GenericRecyclerviewAdapter
import com.e.security.ui.recyclerviews.celldata.StudyPlaceDataVhCell
import com.e.security.ui.recyclerviews.helpers.GenericItemClickListener
import com.e.security.ui.recyclerviews.viewholders.CreateStudyPlacesVh
import com.e.security.ui.utils.addFragment
import com.e.security.ui.viewmodels.effects.Effects
import com.e.security.utils.differentItems
import com.e.security.utils.printIfDbg
import org.apache.poi.util.Units
import org.apache.poi.xwpf.usermodel.ParagraphAlignment
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFTable
import org.apache.poi.xwpf.usermodel.XWPFTableRow
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTabJc
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.math.BigInteger


class MainActivity : BaseActivity() {

    private lateinit var binding: FragmentRecyclerviewAddBtnBinding
    lateinit var mainActivityComponent:MainActivityComponent
    private val viewModel:MainViewModel by lazy(this::getViewModel)
    private lateinit var recyclerviewAdapter:
            GenericRecyclerviewAdapter<StudyPlaceDataVhCell,CreateStudyPlacesVh>
    private var alertDialog:StudyPlaceInfoDialog?=null
    private var TAG=javaClass.name

    override fun onCreate(savedInstanceState: Bundle?) {

        mainActivityComponent=(application as BaseApplication).appComponent.mainActivityComponent().create()
        mainActivityComponent.inject(this)

        super.onCreate(savedInstanceState)
        binding = FragmentRecyclerviewAddBtnBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initUi()
        initStateObserver()
        initEffectObserver()

        viewModel.getStudyPlacesAndTheirFindings()

        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            PackageManager.PERMISSION_GRANTED
        )

        WriteToWord(this).fakeData()

       }

    private fun initEffectObserver() {
        viewModel.viewEffect.observe(this){effect->
            when(effect){
                is Effects.StartReportsFragment->startReportsFragment()
            }

        }
    }

    private fun startReportsFragment() {
        val fragment=StudyPlaceReportsFragment()
        addFragment(fragment,R.id.fragment_container,"StudyPlaceReportsFragment")
    }

    private fun initStateObserver() {
        viewModel.viewState.observe(this){ state->
            val prev=state.prevState
            val curr=state.currentState

            when {

                recyclerviewAdapter.hasNoItems() -> {
                    recyclerviewAdapter.addItems(curr.studyPlacesVhCells)
                }
                prev.studyPlacesVhCells.size < curr.studyPlacesVhCells.size -> {
                    val newItems = curr.studyPlacesVhCells.differentItems(prev.studyPlacesVhCells)
                    recyclerviewAdapter.addItems(newItems)
                }
                prev.studyPlacesVhCells.size > curr.studyPlacesVhCells.size -> {
                    val itemsToRemove =
                    curr.studyPlacesVhCells.differentItems(prev.studyPlacesVhCells)
                    recyclerviewAdapter.removeItems(itemsToRemove)
                }
            }
        }

    }

    private fun initUi() {
        initRecyclerview()

        binding.addBtn.setOnClickListener {
            if (alertDialog==null){
                alertDialog = StudyPlaceInfoDialog()

            }
            alertDialog!!.show(supportFragmentManager,StudyPlaceInfoDialog.TAG)

        }
    }

    private fun initRecyclerview() {
        val recyclerview=binding.recyclerview
        recyclerviewAdapter=GenericRecyclerviewAdapter(R.layout.study_place_vh_cell_design,
            CreateStudyPlacesVh::class.java)
        recyclerviewAdapter.setItemClickListener(object:GenericItemClickListener<StudyPlaceDataVhCell>{
            override fun onItemClick(item: StudyPlaceDataVhCell) {
                viewModel.startReportsFragment(item.id)
            }
        })
        recyclerview.adapter=recyclerviewAdapter
        recyclerview.layoutManager=LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL,
            false)
        recyclerview.setHasFixedSize(true)
    }


    fun writeToWord(imgFile: InputStream) {

        val text = "Textttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt"
          val newText=  text.chunked(10).joinToString("\n")
        println(newText)
        val twipsPerInch: Int =
            1440 //measurement unit for table cell width and tab stop pos is twips (twentieth of an inch point)


        val document = XWPFDocument()
        var paragraph = document.createParagraph()
        var run = paragraph!!.createRun()
        run.setText("Image")

        paragraph = document.createParagraph()
        run = paragraph.createRun()
        run.setText("First using a table:")

        //create table

        //create table
        val table = document.createTable()
        table.width = 6 * twipsPerInch
        //create CTTblGrid for this table with widths of the 2 columns.
        //necessary for Libreoffice/Openoffice to accept the column widths.
        //first column = 2 inches width
        //create CTTblGrid for this table with widths of the 2 columns.
        //necessary for Libreoffice/Openoffice to accept the column widths.
        //first column = 2 inches width
//    table.ctTbl.addNewTblGrid().addNewGridCol().w = BigInteger.valueOf(2 * twipsPerInch.toLong())
        table.ctTbl.addNewTblGrid().addNewGridCol()
        //second column = 4 inches width
        //second column = 4 inches width
//    table.ctTbl.tblGrid.addNewGridCol().w = BigInteger.valueOf(4 * twipsPerInch.toLong())
        table.ctTbl.tblGrid.addNewGridCol()
        //create first row
        //create first row
        val tableRow = table.getRow(0)
        //first cell
        //first cell
        var cell = tableRow.getCell(0)
        //set width for first column = 2 inches
        //set width for first column = 2 inches
        val tblWidth = cell.ctTc.addNewTcPr().addNewTcW()
        tblWidth.w = BigInteger.valueOf(2 * twipsPerInch.toLong())
        //STTblWidth.DXA is used to specify width in twentieths of a point.
        //STTblWidth.DXA is used to specify width in twentieths of a point.
        tblWidth.type = STTblWidth.DXA
        //first paragraph in first cell
        //first paragraph in first cell
        paragraph = cell.getParagraphArray(0)
        if (paragraph == null) paragraph = cell.addParagraph()
        //first run in paragraph having picture
        //first run in paragraph having picture
        run = paragraph.createRun()
        run.addPicture(
            imgFile,
            XWPFDocument.PICTURE_TYPE_PNG,
            "imgFile",
            Units.toEMU(100.0),
            Units.toEMU(100.0)
        )
        //second cell
        //second cell
        cell = tableRow.addNewTableCell()
        cell.text = text

        paragraph = document.createParagraph()

//---------------------------------------------------------------------------------------------------


//---------------------------------------------------------------------------------------------------
        paragraph = document.createParagraph()
        run = paragraph.createRun()
        run.setText("Second using tabulator having tab stops:")

        //create tab stop at 2 inches position

        //create tab stop at 2 inches position
        paragraph = document.createParagraph()
        paragraph.alignment = ParagraphAlignment.LEFT
        var tabStop = paragraph.ctp.pPr.addNewTabs().addNewTab()
        tabStop = paragraph.ctp.pPr.tabs.addNewTab()
        tabStop.setVal(STTabJc.LEFT)
        tabStop.pos = BigInteger.valueOf(2 * twipsPerInch.toLong())
        //first run in paragraph having picture
        //first run in paragraph having picture
        run = paragraph.createRun()
        run.addPicture(
            imgFile,
            XWPFDocument.PICTURE_TYPE_PNG,
            " imgFile",
            Units.toEMU(100.0),
            Units.toEMU(100.0)
        )

        run.addTab()
        //second run
        //second run
        run = paragraph.createRun()
        run.setText(text)
        val filePath = File(getExternalFilesDir(null), "Test2.docx");
        val out = FileOutputStream(filePath)
        document.write(out)
//    if (fileOutputStream != null) {
//      fileOutputStream.flush();
//      fileOutputStream.close();
//    }
        out.close()
        document.close()

    }

    fun test2(imgFile: InputStream) {
        val document = XWPFDocument()

        var paragraph = document.createParagraph()
        var run = paragraph.createRun()
        run.setText("קדימות 0")


        val tableOne: XWPFTable = document.createTable(3, 6)

        val tableOneRowOne = tableOne.getRow(0)


        tableOneRowOne.getCell(1).text =
            "קדימותתתתתתתתתתתתתתתתתתתתתתתתתתתתתתתתתתתתתתתתתתתתתתתתתתתתתתתתתתת"
        tableOneRowOne.getCell(2).text = "קדימות,,,,,,"
        tableOneRowOne.getCell(3).text = "קדימות,,,,,,,,,,"
        tableOneRowOne.getCell(4).text = "קדימות,,,,,,,,,,,,,"
        tableOneRowOne.getCell(5).text = "קדימות,,,,,,,,,,,,,,,,,,,,"


        run = tableOneRowOne.getCell(0).addParagraph().createRun()
        run.addPicture(
            imgFile,
            XWPFDocument.PICTURE_TYPE_PNG,
            "imgFile",
            Units.toEMU(100.0),
            Units.toEMU(200.0)
        )

//    val tableOneRowTwo = tableOne.createRow()
//
////    createNewTableCell(tableOneRowTwo,"ספ")
////    createNewTableCell(tableOneRowTwo,"תחום הבדיקה")
////    createNewTableCell(tableOneRowTwo,"סעיף ברשימת מבדק")
////    createNewTableCell(tableOneRowTwo,"הדרישה")
////    createNewTableCell(tableOneRowTwo,"הממצא, מהותו ומיקומו")
////    createNewTableCell(tableOneRowTwo,"תמונה")
//
//
//   val tableOneRowThree=tableOne.getRow()
//
//   run=  tableOneRowThree.getCell(0).addParagraph().createRun()
//    run.addPicture(
//      imgFile,
//      XWPFDocument.PICTURE_TYPE_PNG,
//      "imgFile",
//      Units.toEMU(200.0),
//      Units.toEMU(200.0)
//    )
//    createNewTableCell(tableOneRowThree,"asffffffffffffffffffffffffffffffffffffffffffffffffffffffffff")
//    createNewTableCell(tableOneRowThree,"asffffffffffffffffffffffffffffffffffffffffffffffffffffffffff")
        val filePath = File(getExternalFilesDir(null), "Test2.docx");
        val out = FileOutputStream(filePath)

        document.write(out)
        out.close()
    }

    fun createNewTableCell(row: XWPFTableRow, text: String) {
        val cell = row.createCell()
        cell.setWidth("10%")
        cell.text = text
    }
}

