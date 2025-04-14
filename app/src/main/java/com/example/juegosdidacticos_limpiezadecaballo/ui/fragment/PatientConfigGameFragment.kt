package com.example.juegosdidacticos_limpiezadecaballo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.juegosdidacticos_limpiezadecaballo.R
import com.example.juegosdidacticos_limpiezadecaballo.data.enums.Voices
import com.example.juegosdidacticos_limpiezadecaballo.data.model.ConfigEntity
import com.example.juegosdidacticos_limpiezadecaballo.data.model.ConfigGameEntity
import com.example.juegosdidacticos_limpiezadecaballo.data.model.PatientEntity
import com.example.juegosdidacticos_limpiezadecaballo.data.model.TherapistEntity
import com.example.juegosdidacticos_limpiezadecaballo.databinding.ConfigGamePatientBinding
import com.example.juegosdidacticos_limpiezadecaballo.ui.viewmodel.UserViewModel
import com.example.juegosdidacticos_limpiezadecaballo.utils.AvatarUtils.getAvatarType
import com.example.juegosdidacticos_limpiezadecaballo.utils.BackgroundMusicPlayer
import com.example.juegosdidacticos_limpiezadecaballo.utils.capitalizeFirstLetter
import kotlinx.coroutines.launch

class PatientConfigGameFragment : Fragment() {

    private var _binding: ConfigGamePatientBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by activityViewModels()
    private var selectedUser: PatientEntity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ConfigGamePatientBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            selectedUser = it.getParcelable("selectedUser", PatientEntity::class.java)
        }
        val gameVolumeSeekBar = binding.gameVolumeSeekBar
        val gameVolumePercentage = binding.gameVolumePercentage
        val voiceVolumeSeekBar = binding.voiceVolumeSeekBar
        val voiceVolumePercentage = binding.voiceVolumePercentage
        val musicVolumeSeekBar = binding.musicVolumeSeekBar
        val musicVolumePercentage = binding.musicVolumePercentage
        var config: ConfigEntity? = null

        selectedUser!!.id.let { id: Int ->
            lifecycleScope.launch {

                val configGame = userViewModel.getGameConfigByPatientId(id)!!

                config = userViewModel.getConfigByPatientId(id)!!

                val gameVolume = configGame.gameVolume
                val voiceVolume = configGame.voiceVolume
                val musicVolume = configGame.musicVolume

                gameVolumeSeekBar.progress = gameVolume
                gameVolumePercentage.text = "$gameVolume%"

                voiceVolumeSeekBar.progress = voiceVolume
                voiceVolumePercentage.text = "$voiceVolume%"

                musicVolumeSeekBar.progress = musicVolume
                musicVolumePercentage.text = "$musicVolume%"

                when (config!!.voices) {
                    Voices.MASCULINE -> binding.voice.check(R.id.masculineVoice)
                    Voices.FEMININE -> binding.voice.check(R.id.feminineVoice)
                }
            }
        }

        gameVolumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                gameVolumePercentage.text = "$progress%"
                BackgroundMusicPlayer.setVolume(musicVolumeSeekBar.progress, progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        voiceVolumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                voiceVolumePercentage.text = "$progress%"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        musicVolumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                musicVolumePercentage.text = "$progress%"
                BackgroundMusicPlayer.setVolume(progress, gameVolumeSeekBar.progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.confirmButton.setOnClickListener {
            val voices = when (binding.voice.checkedRadioButtonId) {
                R.id.masculineVoice -> Voices.MASCULINE
                R.id.feminineVoice -> Voices.FEMININE
                else -> {
                    Voices.MASCULINE
                }
            }

            selectedUser!!.id.let { id: Int ->
                lifecycleScope.launch {
                    userViewModel.updateGameConfig(
                        ConfigGameEntity(
                            patientId = id,
                            gameVolume = gameVolumeSeekBar.progress,
                            voiceVolume = voiceVolumeSeekBar.progress,
                            musicVolume = musicVolumeSeekBar.progress
                        )
                    )
                    config?.let { it ->
                        userViewModel.updateConfig(
                            ConfigEntity(
                                patientId = id,
                                difficulty = it.difficulty,
                                subDifficulty = it.subDifficulty,
                                voices = voices,
                                clues = it.clues,
                            )
                        )
                    }

                    findNavController().navigate(
                        R.id.action_PatientConfigGamePage_to_UserInitPage,
                        Bundle().apply {
                            putParcelable("selectedUser", selectedUser)
                        })
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}