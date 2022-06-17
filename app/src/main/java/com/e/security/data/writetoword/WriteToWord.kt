package com.e.security.data.writetoword


import android.content.Context
import android.content.Intent
import android.net.Uri
import com.e.security.R
import com.e.security.data.FindingDataHolder
import com.e.security.data.ReportDataHolder
import com.e.security.data.StudyPlaceDetailsDataHolder
import com.e.security.data.definitions.rikuzBdikotArray
import com.e.security.utils.printErrorIfDbg
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.CompletableEmitter
import io.reactivex.rxjava3.core.Single
import org.apache.poi.util.Units
import org.apache.poi.xwpf.usermodel.*
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAbstractNum
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STNumberFormat
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.math.BigInteger


class WriteToWord(private val context: Context) {

    private var document: XWPFDocument? = null
    private var table: XWPFTable? = null
    private var paragraph: XWPFParagraph? = null
    private var run: XWPFRun? = null

    private var numID: BigInteger? = null

    fun exportData(): Single<String> {

        return Single.fromCallable {
            createFile()
        }
    }

    private fun createFile(): String {
        return "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    }

    fun save(
        fileUri: Uri, reportDataHolder: ReportDataHolder,
        details: StudyPlaceDetailsDataHolder
    ): Single<String> {
        return Single.fromCallable {
            try {
                val contentResolver = context.contentResolver
                val takeFlags: Int =
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                contentResolver.takePersistableUriPermission(fileUri, takeFlags)
                contentResolver.openFileDescriptor(fileUri, "w")?.use {

                    FileOutputStream(it.fileDescriptor).use {
                        write(reportDataHolder, details)
                        document!!.write(it)
                        it.close()
                        document!!.close()
                    }
                }
            } catch (e: FileNotFoundException) {
                printErrorIfDbg(e)
            } catch (e: IOException) {
                printErrorIfDbg(e)
            }
            context.getString(R.string.saved_successfully)
        }
    }

     fun write(
        reportDataHolder: ReportDataHolder,
        details: StudyPlaceDetailsDataHolder
    ) {
            document = XWPFDocument()

            writeGeneralDetails(details, reportDataHolder.date)

            run = document!!.createParagraph().createRun()
            run!!.addBreak(BreakType.PAGE)
            addText(ParagraphAlignment.CENTER, "פירוט הממצאים")

            reportDataHolder.findingArr.forEach {
                writeFindingListTableTitles(it.values)
            }

            run = document!!.createParagraph().createRun()
            run!!.addBreak(BreakType.PAGE)

            addRikuzBdikotBetihut()
    }


    private fun writeGeneralDetails(details: StudyPlaceDetailsDataHolder, date: String) {
        addText(
            ParagraphAlignment.CENTER,
            "הבטחת תנאים בטיחותיים במוסדות חינוך"
        )
        addText(ParagraphAlignment.CENTER, "דוח סיכום מבדק")
        addText(ParagraphAlignment.LEFT, "נתונים כליים")
        createTable(2, 5)
        // for sapce between tables
        paragraph = document!!.createParagraph()
        table!!.setTableAlignment(TableRowAlign.CENTER)

        var tableRow = table!!.getRow(0)

        tableRow.getCell(0).text = "מספר תלמידים"
        tableRow.getCell(1).text = "סמל המוסד"
        tableRow.getCell(2).text = "שם המוסד"
        tableRow.getCell(3).text = "הבעלות"
        tableRow.getCell(4).text = "הישוב"

        tableRow = table!!.getRow(1)
        tableRow.getCell(0).text = details.studentsNumber
        tableRow.getCell(1).text = details.institutionSymbol
        tableRow.getCell(2).text = details.placeName
        tableRow.getCell(3).text = details.ownership
        tableRow.getCell(4).text = details.city

        createTable(2, 3)
        // for sapce between tables
        paragraph = document!!.createParagraph()
        table!!.setTableAlignment(TableRowAlign.CENTER)

        tableRow = table!!.getRow(0)
        tableRow.getCell(0).text = "טלפון המוסד"
        tableRow.getCell(1).text = "שנת ההקמה"
        tableRow.getCell(2).text = "כתובת המוסד"

        tableRow = table!!.getRow(1)
        tableRow.getCell(0).text = details.studyPlacePhone
        tableRow.getCell(1).text = details.yearOfFounding
        tableRow.getCell(2).text = details.address

        createTable(2, 4)
        // for sapce between tables
        paragraph = document!!.createParagraph()
        table!!.setTableAlignment(TableRowAlign.CENTER)

        tableRow = table!!.getRow(0)
        tableRow.getCell(0).text = "משתתפים מטעם הרשות/ הבעלות"
        tableRow.getCell(1).text = "משתתפים מטעם המוסד החינוכי"
        tableRow.getCell(2).text = "פרטי המפקח/ת הכללי"
        tableRow.getCell(3).text = "פרטי המנהלת"

        tableRow = table!!.getRow(1)
        tableRow.getCell(0).text = details.authorityParticipants
        tableRow.getCell(1).text = details.studyPlaceParticipants
        tableRow.getCell(2).text = details.inspectorDetails
        tableRow.getCell(3).text = details.managerDetails


        createTable(2, 2)
        // for sapce between tables
        paragraph = document!!.createParagraph()
        table!!.setTableAlignment(TableRowAlign.CENTER)


        tableRow = table!!.getRow(0)
        tableRow.getCell(0).text = "פרטי הבודק"
        tableRow.getCell(1).text = "תאריך המבדק "

        tableRow = table!!.getRow(1)
        tableRow.getCell(0).text = details.testerDetails
        tableRow.getCell(1).text = date

        addText(ParagraphAlignment.LEFT, "ממצאים לפי תחומי בדיקה וקדימות טיפול")
        addText(ParagraphAlignment.LEFT, "כללי")
        numID = getNewDecimalNumberingId(BigInteger.valueOf(0), STNumberFormat.DECIMAL)
        setNumberingText("הממצאים אותרו מתוך השוואת המצב הקיים עם סטנדרטים נדרשים המפורטים ברשימות מנחות לעריכת מבדק כפי שמפרסם  האגף למעונות יום ומשפחתונים שבמשרד העבודה, הרווחה והשירותים החברתיים")
        setNumberingText("הממצאים ערוכים לפי תחומי בדיקה וקדימות טיפול באופן הבא:")
        numID = getNewDecimalNumberingId(BigInteger.valueOf(1), STNumberFormat.HEBREW_1)
        setNumberingText("תחומי בדיקה: קישור הממצא לתחום הנבדק בחלוקה המצויה ברשימות המנחות שצוינו לעיל.")
        setNumberingText("קדימות הטיפול: קישור הממצא לקדימות הטיפול על פי הבנתו המקצועית של עורך המבדק, בחלוקה לשלש רמות קדימות אלו:")
        setNumberingText("מבדק הבטיחות הינו עדכני ליום ושעת הבדיקה בלבד!")

        addText(
            ParagraphAlignment.LEFT,
            "קדימות 0: מתייחסת למפגע חמור במיוחד, המחייב להערכת עורך המבדק סגירה מידית של המקום/האתר במוסד החינוך ולאסור שימוש בו עד קבלת הודעה ממנהל הבטיחות ברשות או מנהל המוסד ויועץ בטיחות מטעם הבעלות על המשך שימוש."
        )
        addText(
            ParagraphAlignment.LEFT,
            "קדימות 1: מתייחסות למפגע בטיחותי אשר קיומו מחייב הסרתו המידית."
        )
        addText(
            ParagraphAlignment.LEFT,
            "קדימות 2: מתייחסת לליקוי בטיחותי המחייב טיפול של הרשות המקומית/בעלות בתכנית עבודה סדורה."
        )

    }

    private fun createTable(row: Int, col: Int) {
        table = document!!.createTable(row, col)
    }

    fun writeFindingListTableTitles(findingList: MutableCollection<FindingDataHolder>) {
        paragraph = document!!.createParagraph()
        run = paragraph!!.createRun()
        run!!.setText("קדימות 0")
        //+1 for titles
        table = document!!.createTable(findingList.size + 1, 6)
        val tableRow = table!!.getRow(0)
        tableRow.getCell(0).text = "תמונה"
        tableRow.getCell(1).text = "הממצא, מהותו ומיקומו"
        tableRow.getCell(2).text = "הדרישה"
        tableRow.getCell(3).text = "סעיף ברשימת מבדק"
        tableRow.getCell(4).text = "תחום הבדיקה"
        tableRow.getCell(5).text = "ספ"

        findingList.forEachIndexed(::writeFindings)
    }


    private fun writeFindings(rowIndex: Int, findingDataHolder: FindingDataHolder) {

        val tableRow = table!!.getRow(rowIndex + 1)

        tableRow.getCell(1).text = findingDataHolder.problem.chunked(20).joinToString("\n")
        tableRow.getCell(2).text = findingDataHolder.requirement.chunked(20).joinToString("\n")
        tableRow.getCell(3).text =
            findingDataHolder.sectionInAssessmentList.chunked(20).joinToString("\n")
        tableRow.getCell(4).text = findingDataHolder.testArea.chunked(20).joinToString("\n")
        tableRow.getCell(5).text = (rowIndex + 1).toString()

        run = tableRow.getCell(0).addParagraph().createRun()
        run!!.addPicture(
            context.contentResolver.openInputStream(Uri.parse(findingDataHolder.pic)),
            XWPFDocument.PICTURE_TYPE_PNG,
            "imgFile",
            Units.toEMU(100.0),
            Units.toEMU(200.0)
        )

    }

    private fun getNewDecimalNumberingId(
        abstractNumID: BigInteger?,
        numberingStyle: STNumberFormat.Enum
    ): BigInteger? {
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

    private fun setNumberingText(text: String) {

        paragraph = document!!.createParagraph();
        paragraph!!.numID = numID;
        run = paragraph!!.createRun();
        run!!.setText(text);
    }

    private fun addText(alignment: ParagraphAlignment, text: String) {
        paragraph = document!!.createParagraph()
        paragraph!!.alignment = alignment
        run = paragraph!!.createRun()

        run!!.setText(text)

    }

    private fun addRikuzBdikotBetihut() {
        addText(
            ParagraphAlignment.CENTER,
            "ריכוז בדיקות בטיחות"
        )

        createTable(21, 4)

        var tableRow = table!!.getRow(0)
        tableRow.getCell(3).text = "מס'"
        tableRow.getCell(2).text = "תחום הבדיקה"
        tableRow.getCell(1).text = "תדירות"
        tableRow.getCell(0).text = "הגוף המקצועי הבודק והמאשר"

        rikuzBdikotArray.forEachIndexed { index, rikuzBdikotBetihutObj ->
            tableRow = table!!.getRow(index + 1)
            tableRow.getCell(3).text = (index + 1).toString()
            tableRow.getCell(2).text = rikuzBdikotBetihutObj.testAtea
            tableRow.getCell(1).text = rikuzBdikotBetihutObj.frequency
            tableRow.getCell(0).text = rikuzBdikotBetihutObj.examiningBody
        }
    }


}