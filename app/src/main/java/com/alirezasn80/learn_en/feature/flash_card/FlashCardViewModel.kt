package com.alirezasn80.learn_en.feature.flash_card

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.lifecycle.viewModelScope
import com.alirezasn80.learn_en.R
import com.alirezasn80.learn_en.core.data.database.AppDB
import com.alirezasn80.learn_en.core.domain.local.Define
import com.alirezasn80.learn_en.core.domain.local.Desc
import com.alirezasn80.learn_en.core.domain.local.SheetModel
import com.alirezasn80.learn_en.core.domain.local.Synonym
import com.alirezasn80.learn_en.utill.BaseViewModel
import com.alirezasn80.learn_en.utill.DictCategory
import com.alirezasn80.learn_en.utill.MessageState
import com.alirezasn80.learn_en.utill.toStringList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class FlashCardViewModel @Inject constructor(
    private val database: AppDB,
    private val application: Application,
) : BaseViewModel<FlashCardState>(FlashCardState()) {

    private val wordTS: TextToSpeech = TextToSpeech(application) { status ->
        if (status == TextToSpeech.SUCCESS) wordSetting()
    }

    init {
        getFlashcards()
    }

    private fun wordSetting() {
        wordTS.language = Locale.US
        wordTS.setPitch(1f)
        wordTS.setSpeechRate(1f)
    }

    fun wordSpeak(
        value: String,
        speed: Float
    ) {
        wordTS.setSpeechRate(speed)
        wordTS.speak(value, TextToSpeech.QUEUE_FLUSH, null, null)

    }

    fun setSelectedDictCategory(category: DictCategory) {
        state.update { it.copy(selectedCategory = category) }
    }

    private fun getFlashcards() {
        viewModelScope.launch(Dispatchers.IO) {
            val flashcards = database.wordDao.getFlashcards().map {
                createSheetModel(
                    mainWord = it.word,
                    isHighlight = true,
                    jsonArray = JSONArray(it.definition)
                )
            }

            state.update { it.copy(flashcards = flashcards) }
        }
    }

    fun removeFromFlashcards(word: String) {
        viewModelScope.launch(Dispatchers.IO) {
            database.wordDao.updateFlashcardByWord(0, word)
            val newFlashCards = state.value.flashcards?.filter { it.word != word }
            state.update { it.copy(flashcards = newFlashCards) }
            setMessageBySnackbar(
                message = R.string.removed_from_flashcards,
                messageState = MessageState.Success
            )

        }
    }

    private fun createSheetModel(
        mainWord: String,
        isHighlight: Boolean,
        jsonArray: JSONArray
    ): SheetModel {
        val synonymsModel = mutableListOf<Synonym>()
        val descriptionModel = mutableListOf<Desc>()
        val examples = mutableListOf<String>()


        // Definition------------------------------
        val definition = try {
            jsonArray.getJSONArray(0).getJSONArray(0).getString(0)
        } catch (e: Exception) {
            mainWord
        }


        // Synonyms ------------------------------
        val bdJson: JSONArray? = try {
            jsonArray.getJSONArray(1)
        } catch (e: Exception) {
            null
        }

        if (bdJson != null)
            for (i in 0 until bdJson.length()) {
                val array = bdJson.getJSONArray(i)
                val type = array.getString(0)
                val totalSimilar = array.getJSONArray(2)

                val defines: MutableList<Define> = mutableListOf()

                for (j in 0 until totalSimilar.length()) {
                    val similarTranslate = totalSimilar.getJSONArray(j).getString(0)
                    val synonyms = totalSimilar.getJSONArray(j).getJSONArray(1)
                    val define = Define(similarTranslate, synonyms.toStringList())
                    defines.add(define)
                }

                val synonym = Synonym(type, defines)
                synonymsModel.add(synonym)
                //definesModel.clear()
            }


        //Description of Word ------------------------------
        val mdJson = try {
            jsonArray.getJSONArray(12)
        } catch (e: Exception) {
            null
        }

        if (mdJson != null)
            for (i in 0 until mdJson.length()) {
                val array = mdJson.getJSONArray(i)
                val type = array.getString(0)
                val descriptions = array.getJSONArray(1)

                val texts = mutableListOf<String>()
                for (j in 0 until descriptions.length()) {
                    val desc = descriptions.getJSONArray(j).getString(0)
                    texts.add(desc)
                }
                descriptionModel.add(Desc(type, texts))
            }


        //Example of Word ---------------------------------------
        val exJson = try {
            jsonArray.getJSONArray(13).getJSONArray(0)
        } catch (e: Exception) {
            null
        }

        if (exJson != null)
            for (i in 0 until exJson.length()) {
                val array = exJson.getJSONArray(i)
                val example = array.getString(0)
                examples.add(example)
            }


        //Create Model ----------------------------------------------

        return SheetModel(
            word = mainWord,
            define = definition,
            synonyms = synonymsModel,
            isHighlight = isHighlight,
            descriptions = descriptionModel,
            examples = examples
        )
    }

}