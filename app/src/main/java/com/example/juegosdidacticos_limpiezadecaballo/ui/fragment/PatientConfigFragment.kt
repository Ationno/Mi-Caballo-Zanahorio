package com.example.juegosdidacticos_limpiezadecaballo.ui.fragment

import android.app.AlertDialog
import android.content.Intent
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
import com.example.juegosdidacticos_limpiezadecaballo.GameActivity
import com.example.juegosdidacticos_limpiezadecaballo.MainActivity
import com.example.juegosdidacticos_limpiezadecaballo.R
import com.example.juegosdidacticos_limpiezadecaballo.data.enums.Avatar
import com.example.juegosdidacticos_limpiezadecaballo.data.enums.Difficulty
import com.example.juegosdidacticos_limpiezadecaballo.data.enums.Genre
import com.example.juegosdidacticos_limpiezadecaballo.data.enums.Voices
import com.example.juegosdidacticos_limpiezadecaballo.data.model.ConfigEntity
import com.example.juegosdidacticos_limpiezadecaballo.data.model.ConfigGameEntity
import com.example.juegosdidacticos_limpiezadecaballo.databinding.PatientConfigPageBinding
import com.example.juegosdidacticos_limpiezadecaballo.ui.viewmodel.UserViewModel
import com.example.juegosdidacticos_limpiezadecaballo.data.model.PatientEntity
import com.example.juegosdidacticos_limpiezadecaballo.utils.AvatarUtils
import com.example.juegosdidacticos_limpiezadecaballo.utils.AvatarUtils.getAvatarType
import com.example.juegosdidacticos_limpiezadecaballo.utils.BackgroundMusicPlayer
import com.example.juegosdidacticos_limpiezadecaballo.utils.capitalizeFirstLetter
import kotlinx.coroutines.launch

class PatientConfigFragment : Fragment() {

    private var _binding: PatientConfigPageBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by activityViewModels()
    private var selectedUser: PatientEntity? = null
    private var selectedAvatarId: Int? = null
    private var originalDifficulty: Difficulty? = null
    private var originalSubDifficulty: Difficulty = Difficulty.EASY


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedUser = it.getParcelable("selectedUser", PatientEntity::class.java)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PatientConfigPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectedUser?.let { user ->
            populateUserData(user)

            lifecycleScope.launch {
                val config = userViewModel.getConfigByPatientId(user.id)
                if (config != null) {
                    populateConfigData(config)
                }
            }

            lifecycleScope.launch {
                val configGame = userViewModel.getGameConfigByPatientId(user.id)
                if (configGame != null) {
                    populateVolumesData(configGame)
                }
            }
        }

        setupAvatarSelection()

        binding.gameVolumeSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.gameVolumePercentage.text = "$progress%"
                BackgroundMusicPlayer.setVolume(binding.musicVolumeSeekBar.progress, progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.voiceVolumeSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.voiceVolumePercentage.text = "$progress%"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.musicVolumeSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.musicVolumePercentage.text = "$progress%"
                BackgroundMusicPlayer.setVolume(progress, binding.gameVolumeSeekBar.progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.modify.setOnClickListener {
            modifyPatient()
        }

        binding.delete.setOnClickListener {
            deletePatient()
        }

    }

    private fun setupAvatarSelection() {
        val avatars = listOf(
            binding.commonPatientFields.commonFields.avatar1,
            binding.commonPatientFields.commonFields.avatar2,
            binding.commonPatientFields.commonFields.avatar3,
            binding.commonPatientFields.commonFields.avatar4,
            binding.commonPatientFields.commonFields.avatar5
        )

        AvatarUtils.setupAvatarSelection(avatars) { selectedAvatarId ->
            this.selectedAvatarId = selectedAvatarId
        }
    }

    private fun modifyPatient() {
        val name = binding.commonPatientFields.commonFields.name.text.toString().trim()
            .capitalizeFirstLetter()
        val surname = binding.commonPatientFields.commonFields.surname.text.toString().trim()
            .capitalizeFirstLetter()
        val age = binding.commonPatientFields.age.text.toString().trim().toIntOrNull()
        val observations =
            binding.commonPatientFields.observations.text.toString().trim().capitalizeFirstLetter()
        val genre = when (binding.commonPatientFields.patientGenre.checkedRadioButtonId) {
            R.id.male -> Genre.MALE
            R.id.female -> Genre.FEMALE
            else -> null
        }
        val difficulty = when (binding.difficulty.checkedRadioButtonId) {
            R.id.easy -> Difficulty.EASY
            R.id.medium -> Difficulty.MEDIUM
            R.id.hard -> Difficulty.HARD
            else -> null
        }
        val voices = when (binding.voice.checkedRadioButtonId) {
            R.id.masculineVoice -> Voices.MASCULINE
            R.id.feminineVoice -> Voices.FEMININE
            else -> null
        }

        val clues = when (binding.clues.checkedRadioButtonId) {
            R.id.yes -> true
            R.id.no -> false
            else -> null
        }

        if (name.isEmpty() || surname.isEmpty() || age == null || genre == null || selectedAvatarId == null || difficulty == null || voices == null || clues == null) {
            Toast.makeText(
                requireContext(),
                "Por favor, complete todos los campos obligatorios",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val patient = PatientEntity(
            id = selectedUser!!.id,
            name = name,
            surname = surname,
            age = age,
            observations = observations,
            genre = genre,
            avatar = getAvatarType(selectedAvatarId!!)
        )

    val newSubDifficulty = if (difficulty == originalDifficulty) originalSubDifficulty else Difficulty.EASY

        val config = ConfigEntity(
            patientId = selectedUser!!.id,
            difficulty = difficulty,
            subDifficulty = newSubDifficulty,
            voices = voices,
            clues = clues,
        )
        val gameConfig = ConfigGameEntity(
            patientId = selectedUser!!.id,
            gameVolume = binding.gameVolumeSeekBar.progress,
            voiceVolume = binding.voiceVolumeSeekBar.progress,
            musicVolume = binding.musicVolumeSeekBar.progress
        )

        lifecycleScope.launch {
            try {
                userViewModel.updatePatient(patient)
                userViewModel.updateConfig(config)
                userViewModel.updateGameConfig(gameConfig)
                Toast.makeText(
                    requireContext(),
                    "Paciente modificado exitosamente",
                    Toast.LENGTH_SHORT
                ).show()
                navigateBack()
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Error al modificar el paciente",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun populateUserData(user: PatientEntity) {
        binding.commonPatientFields.commonFields.name.setText(user.name)
        binding.commonPatientFields.commonFields.surname.setText(user.surname)
        binding.commonPatientFields.age.setText(user.age.toString())

        this.selectedAvatarId = AvatarUtils.getAvatarResource(user.avatar)

        when (user.avatar) {
            Avatar.FIRST -> binding.commonPatientFields.commonFields.avatar1.setBackgroundResource(R.drawable.avatar_selected_border)
            Avatar.SECOND -> binding.commonPatientFields.commonFields.avatar2.setBackgroundResource(
                R.drawable.avatar_selected_border
            )

            Avatar.THIRD -> binding.commonPatientFields.commonFields.avatar3.setBackgroundResource(R.drawable.avatar_selected_border)
            Avatar.FOURTH -> binding.commonPatientFields.commonFields.avatar4.setBackgroundResource(
                R.drawable.avatar_selected_border
            )

            Avatar.FIFTH -> binding.commonPatientFields.commonFields.avatar5.setBackgroundResource(R.drawable.avatar_selected_border)

        }

        when (user.genre) {
            Genre.MALE -> binding.commonPatientFields.patientGenre.check(R.id.male)
            Genre.FEMALE -> binding.commonPatientFields.patientGenre.check(R.id.female)
        }
    }

    private fun populateConfigData(config: ConfigEntity) {

        when (config.difficulty) {
            Difficulty.EASY -> binding.difficulty.check(R.id.easy)
            Difficulty.MEDIUM -> binding.difficulty.check(R.id.medium)
            Difficulty.HARD -> binding.difficulty.check(R.id.hard)
        }

        when (config.voices) {
            Voices.MASCULINE -> binding.voice.check(R.id.masculineVoice)
            Voices.FEMININE -> binding.voice.check(R.id.feminineVoice)
        }

        if (config.clues) {
            binding.clues.check(R.id.yes)
        } else {
            binding.clues.check(R.id.no)
        }

        originalDifficulty = config.difficulty
        originalSubDifficulty = config.difficulty
    }

    private fun populateVolumesData(configGame: ConfigGameEntity) {
        val gameVolume = configGame.gameVolume
        val voiceVolume = configGame.voiceVolume
        val musicVolume = configGame.musicVolume

        binding.gameVolumeSeekBar.progress = gameVolume
        binding.gameVolumePercentage.text = "$gameVolume%"

        binding.voiceVolumeSeekBar.progress = voiceVolume
        binding.voiceVolumePercentage.text = "$voiceVolume%"

        binding.musicVolumeSeekBar.progress = musicVolume
        binding.musicVolumePercentage.text = "$musicVolume%"

    }

    private fun deletePatient() {
        val dialogView = layoutInflater.inflate(R.layout.confirm_action, null)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val userName = selectedUser?.name

        val infoTitle = dialogView.findViewById<TextView>(R.id.infoTitle)
        infoTitle.text = "Eliminar a $userName"

        val infoText = dialogView.findViewById<TextView>(R.id.infoText)
        infoText.text =
            "¿Estas seguro que quieres eliminar el perfil de $userName? Se borraran todos sus datos de forma permanente."


        dialogView.findViewById<View>(R.id.confirmButton).setOnClickListener {
            dialog.dismiss()
            lifecycleScope.launch {
                try {
                    userViewModel.deletePatient(selectedUser!!)
                    Toast.makeText(
                        requireContext(),
                        "Paciente eliminado exitosamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    navigateBack()
                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(),
                        "Error al eliminar el paciente",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        dialogView.findViewById<View>(R.id.closeDialogButton).setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()

    }

    private fun navigateBack() {
        findNavController().navigate(R.id.action_PatientConfigPage_to_UserManagementPage)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}