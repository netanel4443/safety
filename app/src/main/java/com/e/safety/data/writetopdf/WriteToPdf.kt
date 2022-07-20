package com.e.safety.data.writetopdf

import android.app.Application
import android.content.Intent
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.Layout
import android.text.StaticLayout
import android.text.TextDirectionHeuristics
import android.text.TextPaint
import androidx.core.graphics.scale
import com.e.safety.R
import com.e.safety.data.ReportDataHolder
import com.e.safety.data.StudyPlaceDetailsDataHolder
import com.e.safety.data.definitions.rikuzBdikotArray
import com.e.safety.di.scopes.ActivityScope
import com.e.safety.utils.printErrorIfDbg
import com.e.safety.utils.printIfDbg
import io.reactivex.rxjava3.core.Single
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
//todo handle a situation where the text can be drawn beyond page width
@ActivityScope
class WriteToPdf @Inject constructor(private val application: Application) {
    private val STARTED_X_LOCATION = 20
    private val A4_WIDTH = 598
    private val PAGE_WIDTH_WITH_MARGIN = 558
    private val A4_HEIGHT = 841

    private var xLocation = STARTED_X_LOCATION
    private var yLocation = 0
    private var biggestCellHeight = 0
    private var textPaint = textPaint()
    private var canvas: Canvas? = null
    private var page: PdfDocument.Page? = null
    private var pdfDoc: PdfDocument? = null
    private var pageInfo: PdfDocument.PageInfo? = null
    private var pageNumber = 1
    private var heightSum = 0
    private var xSpaceCell = 0


    private fun createNewPage() {
        pageInfo = PdfDocument.PageInfo.Builder(A4_WIDTH, A4_HEIGHT, pageNumber).create()
        page = pdfDoc?.startPage(pageInfo)
        canvas = page?.canvas
        pageNumber++
    }

    private fun createPdfDocument() {
        pdfDoc = PdfDocument()
    }

    private fun clearData() {
        xLocation = STARTED_X_LOCATION
        yLocation = 0
        biggestCellHeight = 0
        textPaint = textPaint()
        canvas = null
        page = null
        pdfDoc = null
        pageInfo = null
        pageNumber = 1
        heightSum = 0
        xSpaceCell = 0
    }

    private fun finishPage() {
        pdfDoc!!.finishPage(page)
    }

    private fun fileType(): String {
        return "application/pdf"
    }


    fun createPdf(
        reportDataHolder: ReportDataHolder,
        studyPlaceDetailsDataHolder: StudyPlaceDetailsDataHolder
    ): Single<String> {
        return Single.fromCallable {
            clearData() // clear prev data if the user creates multiple pdf files

            createPdfDocument()

            createNewPage()
            firstPage(studyPlaceDetailsDataHolder, reportDataHolder.date)
            finishPage()

            createNewPage()
            updateXlocation(STARTED_X_LOCATION)
            updateYlocation(0)
            writeFindingListTableTitles(reportDataHolder)
            finishPage()

            createNewPage()
            updateXlocation(STARTED_X_LOCATION)
            updateYlocation(0)
            drawLastPage()
            finishPage()

            //conclusion page
            createNewPage()
            updateXlocation(STARTED_X_LOCATION)
            updateYlocation(0)
            drawConclusionPage(reportDataHolder.conclusion)
            finishPage()

            fileType()

        }
    }

    private fun drawConclusionPage(conclusion:String) {
        drawRight(
            createStaticLayout(application.getString(R.string.to_conclude),
                PAGE_WIDTH_WITH_MARGIN,
                Layout.Alignment.ALIGN_NORMAL
            )
        )
        drawRight(
            createStaticLayout(conclusion,
                PAGE_WIDTH_WITH_MARGIN,
                Layout.Alignment.ALIGN_NORMAL
            )
        )
        drawRight(
            createStaticLayout("בברכה,",
                PAGE_WIDTH_WITH_MARGIN,
                Layout.Alignment.ALIGN_NORMAL
            )
        )
        drawRight(
            createStaticLayout("חתימת יועץ בטיחות:",
                PAGE_WIDTH_WITH_MARGIN,
                Layout.Alignment.ALIGN_NORMAL
            )
        )

    }

    fun save(
        fileUri: Uri
    ): Single<String> {
        return Single.fromCallable {
            try {

                val contentResolver = application.contentResolver
                val takeFlags: Int =
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                contentResolver.takePersistableUriPermission(fileUri, takeFlags)
                contentResolver.openFileDescriptor(fileUri, "w")?.use {

                    FileOutputStream(it.fileDescriptor).use {
                        pdfDoc?.writeTo(it)
//                       pdfDoc?.close()
                        printIfDbg("writeToPDF", "pdf created")
                        it.close()

                    }
                }
            } catch (e: FileNotFoundException) {
                printErrorIfDbg(e)
            } catch (e: IOException) {
                printErrorIfDbg(e)
            }
            application.getString(R.string.saved_successfully)
        }
    }

    private fun firstPage(
        studyPlaceDetailsDataHolder: StudyPlaceDetailsDataHolder, date: String
    ) {
        drawCenteredText(
            createStaticLayout(
                "הבטחת תנאים בטיחותיים במוסדות חינוך",
                PAGE_WIDTH_WITH_MARGIN,
                Layout.Alignment.ALIGN_CENTER
            )
        )
        addYMargin(20)
        drawCenteredText(
            createStaticLayout(
                "דוח סיכום מבדק",
                PAGE_WIDTH_WITH_MARGIN,
                Layout.Alignment.ALIGN_CENTER
            )
        )
        addYMargin(20)
        drawCenteredText(
            createStaticLayout(
                "נתונים כלליים",
                PAGE_WIDTH_WITH_MARGIN,
                Layout.Alignment.ALIGN_CENTER
            )
        )
        addYMargin(20)


        val staticLayoutArray = ArrayList<StaticLayout>()

        calculateXspaceBetweenCells(5)
        staticLayoutArray.add(prepareTableCellForDrawing(application.getString(R.string.students_number)))
        staticLayoutArray.add(prepareTableCellForDrawing(application.getString(R.string.symbol)))
        staticLayoutArray.add(prepareTableCellForDrawing(application.getString(R.string.place_name)))
        staticLayoutArray.add(prepareTableCellForDrawing(application.getString(R.string.ownership)))
        staticLayoutArray.add(prepareTableCellForDrawing(application.getString(R.string.city)))

        drawTableRow(staticLayoutArray)
        //clear prev data
        staticLayoutArray.clear()

        calculateXspaceBetweenCells(5)
        staticLayoutArray.add(prepareTableCellForDrawing(studyPlaceDetailsDataHolder.studentsNumber))
        staticLayoutArray.add(prepareTableCellForDrawing(studyPlaceDetailsDataHolder.institutionSymbol))
        staticLayoutArray.add(prepareTableCellForDrawing(studyPlaceDetailsDataHolder.placeName))
        staticLayoutArray.add(prepareTableCellForDrawing(studyPlaceDetailsDataHolder.ownership))
        staticLayoutArray.add(prepareTableCellForDrawing(studyPlaceDetailsDataHolder.city))

        drawTableRow(staticLayoutArray)
        //clear prev data
        staticLayoutArray.clear()

        addYMargin(20)

        calculateXspaceBetweenCells(3)
        staticLayoutArray.add(prepareTableCellForDrawing(application.getString(R.string.study_place_phone)))
        staticLayoutArray.add(prepareTableCellForDrawing(application.getString(R.string.founding_year)))
        staticLayoutArray.add(prepareTableCellForDrawing(application.getString(R.string.address)))

        drawTableRow(staticLayoutArray)
        //clear prev data
        staticLayoutArray.clear()

        calculateXspaceBetweenCells(3)
        staticLayoutArray.add(prepareTableCellForDrawing(studyPlaceDetailsDataHolder.studyPlacePhone))
        staticLayoutArray.add(prepareTableCellForDrawing(studyPlaceDetailsDataHolder.yearOfFounding))
        staticLayoutArray.add(prepareTableCellForDrawing(studyPlaceDetailsDataHolder.address))

        drawTableRow(staticLayoutArray)
        //clear prev data
        staticLayoutArray.clear()

        addYMargin(20)

        calculateXspaceBetweenCells(4)
        staticLayoutArray.add(prepareTableCellForDrawing(application.getString(R.string.authority_participants)))
        staticLayoutArray.add(prepareTableCellForDrawing(application.getString(R.string.study_place_participants)))
        staticLayoutArray.add(prepareTableCellForDrawing(application.getString(R.string.inspector_details)))
        staticLayoutArray.add(prepareTableCellForDrawing(application.getString(R.string.manager_details)))

        drawTableRow(staticLayoutArray)
        //clear prev data
        staticLayoutArray.clear()

        calculateXspaceBetweenCells(4)
        staticLayoutArray.add(prepareTableCellForDrawing(studyPlaceDetailsDataHolder.authorityParticipants))
        staticLayoutArray.add(prepareTableCellForDrawing(studyPlaceDetailsDataHolder.studyPlaceParticipants))
        staticLayoutArray.add(prepareTableCellForDrawing(studyPlaceDetailsDataHolder.inspectorDetails))
        staticLayoutArray.add(prepareTableCellForDrawing(studyPlaceDetailsDataHolder.managerDetails))

        drawTableRow(staticLayoutArray)
        //clear prev data
        staticLayoutArray.clear()

        addYMargin(20)

        calculateXspaceBetweenCells(2)
        staticLayoutArray.add(prepareTableCellForDrawing(application.getString(R.string.tester_details)))
        staticLayoutArray.add(prepareTableCellForDrawing(application.getString(R.string.date)))

        drawTableRow(staticLayoutArray)
        //clear prev data
        staticLayoutArray.clear()

        calculateXspaceBetweenCells(2)
        staticLayoutArray.add(prepareTableCellForDrawing(studyPlaceDetailsDataHolder.testerDetails))
        staticLayoutArray.add(prepareTableCellForDrawing(date))

        drawTableRow(staticLayoutArray)

        addYMargin(20)

        drawExplanationOnFindings()
    }

    private fun drawExplanationOnFindings() {
        val staticLayoutArray = ArrayList<StaticLayout>()

        staticLayoutArray.add(
            createStaticLayout(
                "ממצאים לפי תחומי בדיקה וקדימות טיפול",
                PAGE_WIDTH_WITH_MARGIN,
                Layout.Alignment.ALIGN_NORMAL
            )
        )

        staticLayoutArray.add(
            createStaticLayout(
                "כללי",
                PAGE_WIDTH_WITH_MARGIN,
                Layout.Alignment.ALIGN_NORMAL
            )
        )
        staticLayoutArray.add(
            createStaticLayout(
                "1. הממצאים אותרו מתוך השוואת המצב הקיים עם סטנדרטים נדרשים המפורטים ברשימות מנחות לעריכת מבדק כפי שמפרסם  האגף למעונות יום ומשפחתונים שבמשרד העבודה, הרווחה והשירותים החברתיים.",
                PAGE_WIDTH_WITH_MARGIN,
                Layout.Alignment.ALIGN_NORMAL
            )
        )
        staticLayoutArray.add(
            createStaticLayout(
                "2. הממצאים ערוכים לפי תחומי בדיקה וקדימות טיפול באופן הבא:",
                PAGE_WIDTH_WITH_MARGIN,
                Layout.Alignment.ALIGN_NORMAL
            )
        )
        staticLayoutArray.add(
            createStaticLayout(
                "א. תחומי בדיקה: קישור הממצא לתחום הנבדק בחלוקה המצויה ברשימות המנחות שצוינו לעיל.",
                PAGE_WIDTH_WITH_MARGIN,
                Layout.Alignment.ALIGN_NORMAL
            )
        )
        staticLayoutArray.add(
            createStaticLayout(
                "ב. קדימות הטיפול: קישור הממצא לקדימות הטיפול על פי הבנתו המקצועית של עורך המבדק, בחלוקה לשלש רמות קדימות אלו:",
                PAGE_WIDTH_WITH_MARGIN,
                Layout.Alignment.ALIGN_NORMAL
            )
        )
        staticLayoutArray.add(
            createStaticLayout(
                "ג. מבדק הבטיחות הינו עדכני ליום ושעת הבדיקה בלבד!",
                PAGE_WIDTH_WITH_MARGIN,
                Layout.Alignment.ALIGN_NORMAL
            )
        )
        staticLayoutArray.add(
            createStaticLayout(
                "קדימות 0 : מתייחסת למפגע חמור במיוחד, המחייב להערכת עורך המבדק סגירה מידית של המקום/האתר במוסד החינוך ולאסור שימוש בו עד קבלת הודעה ממנהל הבטיחות ברשות או מנהל המוסד ויועץ בטיחות מטעם הבעלות על המשך שימוש.",
                PAGE_WIDTH_WITH_MARGIN,
                Layout.Alignment.ALIGN_NORMAL
            )
        )
        staticLayoutArray.add(
            createStaticLayout(
                "קדימות 1: מתייחסות למפגע בטיחותי אשר קיומו מחייב הסרתו המידית.",
                PAGE_WIDTH_WITH_MARGIN,
                Layout.Alignment.ALIGN_NORMAL
            )
        )
        staticLayoutArray.add(
            createStaticLayout(
                "קדימות 2: מתייחסת לליקוי בטיחותי המחייב טיפול של הרשות המקומית/בעלות בתכנית עבודה סדורה.",
                PAGE_WIDTH_WITH_MARGIN,
                Layout.Alignment.ALIGN_NORMAL
            )
        )

        staticLayoutArray.forEach {
            draw(it)
            addYMargin(it.height + 5) //+5 for height margin between lines
        }

    }

    private fun drawLastPage() {

        calculateXspaceBetweenCells(3)

        val staticLayoutArray = ArrayList<StaticLayout>(3)
        staticLayoutArray.add(prepareTableCellForDrawing(application.getString(R.string.test_area)))
        staticLayoutArray.add(prepareTableCellForDrawing(application.getString(R.string.frequency)))
        staticLayoutArray.add(prepareTableCellForDrawing(application.getString(R.string.examine_body)))
        drawTableRow(staticLayoutArray)

        staticLayoutArray.clear()

        rikuzBdikotArray.forEachIndexed { index, rikuzBdikotBetihutObj ->

            staticLayoutArray.add(prepareTableCellForDrawing(rikuzBdikotBetihutObj.testAtea))
            staticLayoutArray.add(prepareTableCellForDrawing(rikuzBdikotBetihutObj.frequency))
            staticLayoutArray.add(prepareTableCellForDrawing(rikuzBdikotBetihutObj.examiningBody))

            drawTableRow(staticLayoutArray)
            staticLayoutArray.clear()
        }
    }


    private fun drawTableRow(staticLayoutArray: ArrayList<StaticLayout>) {
        staticLayoutArray.forEach {
            draw(it)
            drawTableRectangle(
                xLocation.toFloat(),
                yLocation.toFloat(),
                xLocation.toFloat() + it.width,
                yLocation.toFloat() + biggestCellHeight
            )
            addToXLocation()
        }
        addToYLocation(biggestCellHeight)
        resetRequiredParamsForNewTableLine()
    }

    /** reset x location to 0 and the biggest cell height of its line*/
    private fun resetRequiredParamsForNewTableLine() {
        biggestCellHeight = 0 // reset for new line
        updateXlocation(STARTED_X_LOCATION) //reset
    }

    private fun writeFindingListTableTitles(
        reportDataHolder: ReportDataHolder
    ) {
        calculateXspaceBetweenCells(6)

        reportDataHolder.findingArr.forEachIndexed { priority, findings ->
            drawCenteredText(
                createStaticLayout(
                    "קדימות $priority",
                    300,
                    Layout.Alignment.ALIGN_CENTER
                )
            )
            addYMargin(20)
            updateXlocation(STARTED_X_LOCATION)

            val staticLayoutArray = ArrayList<StaticLayout>()
            staticLayoutArray.add(prepareTableCellForDrawing(application.getString(R.string.pic)))
            staticLayoutArray.add(prepareTableCellForDrawing(application.getString(R.string.problem)))
            staticLayoutArray.add(prepareTableCellForDrawing(application.getString(R.string.requirement)))
            staticLayoutArray.add(prepareTableCellForDrawing(application.getString(R.string.sectionInAssessmentList)))
            staticLayoutArray.add(prepareTableCellForDrawing(application.getString(R.string.test_area)))
            staticLayoutArray.add(prepareTableCellForDrawing(application.getString(R.string.section)))

            drawTableRow(staticLayoutArray)

            findings.values.forEachIndexed { index, findingDataHolder ->
                val findingLayoutArray = ArrayList<StaticLayout>()

                findingLayoutArray.add(prepareTableCellForDrawing(findingDataHolder.problem))
                findingLayoutArray.add(prepareTableCellForDrawing(findingDataHolder.requirement))
                findingLayoutArray.add(prepareTableCellForDrawing(findingDataHolder.sectionInAssessmentList))
                findingLayoutArray.add(prepareTableCellForDrawing(findingDataHolder.testArea))
                findingLayoutArray.add(prepareTableCellForDrawing((index + 1).toString()))

                val imageHeight = 150
                shouldUpdateBiggestCellHeight(imageHeight)

                if (findingDataHolder.problemImages.isNotEmpty()) {
                    drawImageUri(
                        findingDataHolder.problemImages[0], xSpaceCell,
                        imageHeight,
                        xLocation.toFloat(),
                        yLocation.toFloat()
                    )
                }
                //draw rectangle for the image
                drawTableRectangle(
                    xLocation.toFloat(),
                    yLocation.toFloat(),
                    xLocation.toFloat() + xSpaceCell.toFloat(),
                    yLocation.toFloat() + biggestCellHeight.toFloat()
                )
                // increment x location for the rest of the row cells
                addToXLocation()
                // draw the rest of the cells
                drawTableRow(findingLayoutArray)

                // limit images to 6 images , 6 because the number of cells ,
                // the logic can be changed , but for now 6 images is enough.
                var i = 1
                // these lines have the same height , we don't need to calculate here the biggest height
                // of all of the cells
                while (i < findingDataHolder.problemImages.size && i < 7) {
                    drawImageUri(
                        findingDataHolder.problemImages[i], xSpaceCell,
                        imageHeight,
                        xLocation.toFloat(),
                        yLocation.toFloat()
                    )
                    // draw rectangle for the image
                    drawTableRectangle(
                        xLocation.toFloat(),
                        yLocation.toFloat(),
                        xLocation.toFloat() + xSpaceCell.toFloat(),
                        yLocation.toFloat() + imageHeight
                    )
                    addToXLocation()
                    i++
                }
                // calculate the new y location for next row
                addToYLocation(imageHeight)
                resetRequiredParamsForNewTableLine()
            }
            addYMargin(20)
        }
    }


    private fun drawImageUri(path: String, width: Int, height: Int, x: Float, y: Float) {
        var yLoc = y
        // biggestCellHeight because we want all table cells to be drawn at the same line
        // if we will use anything different from biggestCellHeight this calculation
        // is not going to be calculated equally for all row`s cells

        if (biggestCellHeight + heightSum > pageInfo!!.pageHeight) {
            finishPage()
            createNewPage()
            resetParametersForNewPage()
            yLoc = 0f
        }

        val rect = Rect()
        rect.set(0, 0, width, height)

        val bitmap: Bitmap? =

            if (Build.VERSION.SDK_INT < 28) {
               MediaStore.Images.Media.getBitmap(
                    application.contentResolver, Uri.parse(path)
                )
            } else {
                val source = ImageDecoder.createSource(application.contentResolver, Uri.parse(path))
                try {
                    val bitmap = ImageDecoder.decodeBitmap(source)
                    bitmap.copy(Bitmap.Config.ARGB_8888, true)
                }catch (e:Exception){
                    println(e.message)
                 null
                }
            }
            if (bitmap == null){ return }

        val wid: Int = bitmap.width
        val hei: Int = bitmap.height

        val newWidth = wid / 2
        val newHeight = hei / 2
        val scaledBitmap = bitmap.scale(newWidth, newHeight) // decrease bitmap size by 50%

        canvas!!.save()
        updateCanvasXYCoordinates(x, yLoc)
        canvas?.drawBitmap(scaledBitmap, null, rect, null)
        canvas!!.restore()
    }

    private fun drawRight(staticLayout:StaticLayout){
        val layoutWidth = staticLayout.width
        val rightBorder = PAGE_WIDTH_WITH_MARGIN
        val drawLocation = rightBorder - layoutWidth // right border is always greater
        drawAndPrepareForNewParagraph(staticLayout,drawLocation)
    }

    private fun drawCenteredText(staticLayout: StaticLayout) {
        val layoutWidth = staticLayout.width / 2
        val halfPageWidth = PAGE_WIDTH_WITH_MARGIN / 2
        val drawLocation = Math.abs(layoutWidth - halfPageWidth)
        drawAndPrepareForNewParagraph(staticLayout,drawLocation)
    }

    private fun drawAndPrepareForNewParagraph(staticLayout: StaticLayout, drawLocation:Int){
        updateXlocation(drawLocation)
        draw(staticLayout)
        addToYLocation(staticLayout.height)
        updateXlocation(STARTED_X_LOCATION) //reset- return to (margin left,y)
    }

    private fun prepareTableCellForDrawing(text: String): StaticLayout {
        val layoutToDraw = createMultiLineText(text)
        shouldUpdateBiggestCellHeight(layoutToDraw.height)
        return layoutToDraw
    }

    private fun draw(staticLayout: StaticLayout) {

        if (biggestCellHeight + heightSum > pageInfo!!.pageHeight) {
            finishPage()
            createNewPage()
            resetParametersForNewPage()
        }
        canvas!!.save()
        updateCanvasXYCoordinates(xLocation.toFloat(), yLocation.toFloat())
        staticLayout.draw(canvas)
        canvas!!.restore()
    }


    private fun shouldUpdateBiggestCellHeight(height: Int) {
        if (height > biggestCellHeight) biggestCellHeight = height

    }

    private fun createMultiLineText(string: String): StaticLayout {
        return createStaticLayout(string, xSpaceCell, Layout.Alignment.ALIGN_CENTER)
    }

    private fun createStaticLayout(
        text: String,
        widthLimit: Int,
        alignment: Layout.Alignment
    ): StaticLayout {

        val sb = StaticLayout.Builder.obtain(
            text, 0, text.length, textPaint, widthLimit
        )
            .setAlignment(alignment)
            .setTextDirection(TextDirectionHeuristics.RTL)
            .setLineSpacing(1f, 1f)
            .setIncludePad(false)


        return sb.build()
    }


    private fun updateCanvasXYCoordinates(xLocation: Float, yLocation: Float) {
        canvas!!.translate(xLocation, yLocation)
    }

    private fun addToYLocation(additionalHeight: Int) {
        heightSum += additionalHeight
        yLocation += additionalHeight
    }

    private fun updateYlocation(location: Int) {
        heightSum = location
        yLocation = location
    }

    private fun updateXlocation(location: Int) {
        xLocation = location
    }

    private fun addToXLocation() {
        xLocation += xSpaceCell
    }

    private fun resetParametersForNewPage() {
        //for rikuz bdikot we need to continue with the current cell location on the table
        // so we don't reset x location
        yLocation = 0 // reset for new page
        heightSum = 0
    }

    private fun calculateXspaceBetweenCells(numberOfCells: Int) {
        xSpaceCell = PAGE_WIDTH_WITH_MARGIN / numberOfCells
    }

    private fun addYMargin(margin: Int) {
        addToYLocation(margin)
    }

    private fun textPaint(): TextPaint {

        val tf = Typeface.createFromAsset(application.assets, "font/davidlibre.ttf")
        val textPaint = TextPaint()

        textPaint.typeface = tf

        return textPaint
    }

    private fun drawTableRectangle(startX: Float, startY: Float, stopX: Float, stopY: Float) {
        val paint = Paint()
        paint.isAntiAlias = true;
        paint.strokeWidth = 1f;
        paint.color = Color.BLACK;
        paint.style = Paint.Style.STROKE;
        canvas!!.drawRect(startX, startY, stopX, stopY, paint)
    }

}