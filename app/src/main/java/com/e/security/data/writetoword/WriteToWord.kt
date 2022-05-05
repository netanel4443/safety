package com.e.security.data.writetoword

import android.content.Context
import android.net.Uri
import com.e.security.data.FindingDataHolder
import com.e.security.data.FindingListDataHolder
import com.e.security.data.GeneralReportDetailsDataHolder
import com.e.security.utils.printErrorIfDbg
import com.e.security.utils.printIfDbg
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.apache.poi.util.Units
import org.apache.poi.xwpf.usermodel.*
import org.bson.types.ObjectId
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAbstractNum
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STNumberFormat
import java.io.File
import java.io.FileOutputStream
import java.math.BigInteger


class WriteToWord(private val context: Context) {

    private var document:XWPFDocument?=null
    private var table:XWPFTable?=null
    private var paragraph:XWPFParagraph?=null
    private var run:XWPFRun?=null
    private var numID:BigInteger?=null

    fun exportData(findingListDataHolder: FindingListDataHolder,
                   generalDetails: GeneralReportDetailsDataHolder):Completable{

         return   Completable.fromAction {


             document = XWPFDocument()

             writeGeneralDetails(generalDetails)

             findingListDataHolder.findingArr.forEach {
                 writeList(it.values)
             }
             saveWordFileToDirectory()

            }

    }

    private fun writeGeneralDetails( generalDetails: GeneralReportDetailsDataHolder) {
        addText(ParagraphAlignment.CENTER,
        "הבטחת תנאים בטיחותיים במוסדות חינוך")
        addText(ParagraphAlignment.CENTER,"דוח סיכום מבדק")
        addText(ParagraphAlignment.LEFT,"נתונים כליים")
        createTable(2,5)
        // for sapce between tables
        paragraph=document!!.createParagraph()
        table!!.setTableAlignment(TableRowAlign.CENTER)

        var tableRow = table!!.getRow(0)

        tableRow.getCell(0).text="מספר תלמידים"
        tableRow.getCell(1).text ="סמל המוסד"
        tableRow.getCell(2).text = "שם המוסד"
        tableRow.getCell(3).text = "הבעלות"
        tableRow.getCell(4).text = "הישוב"

        tableRow = table!!.getRow(1)
        tableRow.getCell(0).text=""
        tableRow.getCell(1).text =generalDetails.institutionSymbol
        tableRow.getCell(2).text = generalDetails.placeName
        tableRow.getCell(3).text = ""
        tableRow.getCell(4).text = generalDetails.city

        createTable(2,3)
        // for sapce between tables
        paragraph=document!!.createParagraph()
        table!!.setTableAlignment(TableRowAlign.CENTER)

        tableRow = table!!.getRow(0)
        tableRow.getCell(0).text ="טלפון המוסד"
        tableRow.getCell(1).text ="שנת ההקמה"
        tableRow.getCell(2).text="כתובת המוסד"

        createTable(2,4)
        // for sapce between tables
        paragraph=document!!.createParagraph()
        table!!.setTableAlignment(TableRowAlign.CENTER)

        tableRow = table!!.getRow(0)
        tableRow.getCell(0).text="משתתפים מטעם הרשות/ הבעלות"
        tableRow.getCell(1).text ="משתתפים מטעם המוסד החינוכי"
        tableRow.getCell(2).text ="פרטי המפקח/ת הכללי"
        tableRow.getCell(3).text ="פרטי המנהלת"

        createTable(2,2)
        // for sapce between tables
        paragraph=document!!.createParagraph()
        table!!.setTableAlignment(TableRowAlign.CENTER)


        tableRow = table!!.getRow(0)
        tableRow.getCell(0).text="פרטי הבודק"
        tableRow.getCell(1).text ="תאריך המבדק "

        tableRow = table!!.getRow(1)
        tableRow.getCell(0).text=generalDetails.testerDetails
        tableRow.getCell(1).text =generalDetails.date

        addText(ParagraphAlignment.LEFT,"ממצאים לפי תחומי בדיקה וקדימות טיפול")
        addText(ParagraphAlignment.LEFT,"כללי")
         numID = getNewDecimalNumberingId(BigInteger.valueOf(0),STNumberFormat.DECIMAL)
        setNumberingText("הממצאים אותרו מתוך השוואת המצב הקיים עם סטנדרטים נדרשים המפורטים ברשימות מנחות לעריכת מבדק כפי שמפרסם  האגף למעונות יום ומשפחתונים שבמשרד העבודה, הרווחה והשירותים החברתיים")
        setNumberingText("הממצאים ערוכים לפי תחומי בדיקה וקדימות טיפול באופן הבא:")
        numID=getNewDecimalNumberingId(BigInteger.valueOf(1),STNumberFormat.HEBREW_1)
        setNumberingText("תחומי בדיקה: קישור הממצא לתחום הנבדק בחלוקה המצויה ברשימות המנחות שצוינו לעיל.")
        setNumberingText("קדימות הטיפול: קישור הממצא לקדימות הטיפול על פי הבנתו המקצועית של עורך המבדק, בחלוקה לשלש רמות קדימות אלו:")
        setNumberingText("מבדק הבטיחות הינו עדכני ליום ושעת הבדיקה בלבד!")

        addText(ParagraphAlignment.LEFT,
            "קדימות 0: מתייחסת למפגע חמור במיוחד, המחייב להערכת עורך המבדק סגירה מידית של המקום/האתר במוסד החינוך ולאסור שימוש בו עד קבלת הודעה ממנהל הבטיחות ברשות או מנהל המוסד ויועץ בטיחות מטעם הבעלות על המשך שימוש.")
        addText(ParagraphAlignment.LEFT,
            "קדימות 1: מתייחסות למפגע בטיחותי אשר קיומו מחייב הסרתו המידית."
        )
        addText(ParagraphAlignment.LEFT,
            "קדימות 2: מתייחסת לליקוי בטיחותי המחייב טיפול של הרשות המקומית/בעלות בתכנית עבודה סדורה."
        )
        addText(ParagraphAlignment.CENTER,"פירוט הממצאים")
    }

    private fun createTable(row:Int,col:Int){
        table = document!!.createTable(row, col)
    }

    private fun addText(alignment: ParagraphAlignment,text:String){
        paragraph = document!!.createParagraph()
        paragraph!!.alignment= alignment
        run = paragraph!!.createRun()
        run!!.fontFamily = "David"
        run!!.setText(text)
    }


    fun writeList(findingList: MutableCollection<FindingDataHolder>){
         paragraph = document!!.createParagraph()
         run = paragraph!!.createRun()
         run!!.setText("קדימות 0")
        //+1 for titles
        table = document!!.createTable(findingList.size+1, 6)
        val tableRow = table!!.getRow(0)
        tableRow.getCell(0).text = "תמונה"
        tableRow.getCell(1).text="הממצא, מהותו ומיקומו"
        tableRow.getCell(2).text ="סעיף ברשימת מבדק"
        tableRow.getCell(3).text = "הדרישה"
        tableRow.getCell(4).text = "תחום הבדיקה"
        tableRow.getCell(5).text = "ספ"
        //todo need to change it to הממצא, מהותו ומיקומו


        findingList.forEachIndexed(::write)


    }

    private fun write(rowIndex:Int,findingDataHolder: FindingDataHolder){

            val tableRow = table!!.getRow(rowIndex+1)

            tableRow.getCell(1).text = findingDataHolder.problem.chunked(10).joinToString("\n")
            tableRow.getCell(2).text = findingDataHolder.requirement.chunked(10).joinToString("\n")
        //todo need to change it to הממצא, מהותו ומיקומו
            tableRow.getCell(3).text = findingDataHolder.requirement.chunked(10).joinToString("\n")
            tableRow.getCell(4).text = findingDataHolder.sectionInAssessmentList.chunked(10).joinToString("\n")
            tableRow.getCell(5).text = findingDataHolder.section


            run = tableRow.getCell(0).addParagraph().createRun()
            run!!.addPicture(
               context.contentResolver.openInputStream(Uri.parse(findingDataHolder.pic)),
                XWPFDocument.PICTURE_TYPE_PNG,
                "imgFile",
                Units.toEMU(100.0),
                Units.toEMU(200.0)
            )

        }

     private fun saveWordFileToDirectory(){
        val filePath = File(context.getExternalFilesDir(null), "Test2.docx");
        val out = FileOutputStream(filePath)

        document!!.write(out)
        out.close()
    }

    fun fakeData(){
        var findingPriorityList: HashMap<ObjectId,FindingDataHolder> = HashMap()

       val fdh= FindingDataHolder(problem = "bla bla bla bla",
            pic = "content://media/external/images/media/44")

        for (i in 0..3){
            findingPriorityList[ObjectId()]=fdh
        }
        var fdlh= FindingListDataHolder()
        fdlh.findingArr.forEach {
            it.putAll(findingPriorityList)
        }
        val generalDetails=GeneralReportDetailsDataHolder(
            "אופקים",
            "בית ספר אשלים","1234","05.05.22","שמוליק בן יוסי"
        )

        exportData(fdlh,generalDetails)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                        printIfDbg("WriteToWord","success")
            },{ printErrorIfDbg("WriteToWord",it.message)})


    }


    private fun getNewDecimalNumberingId( abstractNumID: BigInteger?,
                                          numberingStyle: STNumberFormat.Enum): BigInteger? {
        var abstractNumID: BigInteger? = abstractNumID
        val cTAbstractNum = CTAbstractNum.Factory.newInstance()
        cTAbstractNum.abstractNumId = abstractNumID
        val cTLvl = cTAbstractNum.addNewLvl()
        cTLvl.ilvl = BigInteger.valueOf(0) // set indent level 0
        cTLvl.addNewNumFmt().setVal(numberingStyle)
        cTLvl.addNewLvlText().setVal("%1.")
        cTLvl.addNewStart().setVal(BigInteger.valueOf(1))
        val abstractNum = XWPFAbstractNum(cTAbstractNum)
        val numbering = document!!.createNumbering()
        abstractNumID = numbering.addAbstractNum(abstractNum)
        return numbering.addNum(abstractNumID)
    }

   private fun setNumberingText(text:String){

        paragraph = document!!.createParagraph();
        paragraph!!.numID = numID;
        run = paragraph!!.createRun();
        run!!.setText(text);
    }

}