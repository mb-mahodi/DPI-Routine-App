package com.example.data.local

import android.content.Context
import androidx.room.*
import com.example.data.model.ClassSession
import com.example.data.model.Subject
import com.example.data.model.Teacher
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "class_sessions")
data class ClassSessionEntity(
    @PrimaryKey val id: String,
    val dayOfWeek: String,
    val periodNumber: Int,
    val periodSpan: Int,
    val startTime: String,
    val endTime: String,
    val subjectCode: String,
    val subjectName: String,
    val teacherName: String,
    val roomNumber: String,
    val isPractical: Boolean,
    val note: String
) {
    fun toModel(): ClassSession = ClassSession(
        id = id,
        dayOfWeek = dayOfWeek,
        periodNumber = periodNumber,
        periodSpan = periodSpan,
        startTime = startTime,
        endTime = endTime,
        subjectCode = subjectCode,
        subjectName = subjectName,
        teacherName = teacherName,
        roomNumber = roomNumber,
        isPractical = isPractical,
        note = note
    )

    companion object {
        fun fromModel(model: ClassSession): ClassSessionEntity = ClassSessionEntity(
            id = model.id,
            dayOfWeek = model.dayOfWeek,
            periodNumber = model.periodNumber,
            periodSpan = model.periodSpan,
            startTime = model.startTime,
            endTime = model.endTime,
            subjectCode = model.subjectCode,
            subjectName = model.subjectName,
            teacherName = model.teacherName,
            roomNumber = model.roomNumber,
            isPractical = model.isPractical,
            note = model.note
        )
    }
}

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey val code: String,
    val slNo: Int,
    val name: String,
    val banglaName: String,
    val teacherName: String,
    val theoryHours: Int,
    val practicalHours: Int,
    val credits: Int,
    val theoryCont: Int,
    val theoryFinal: Int,
    val practicalCont: Int,
    val practicalFinal: Int,
    val totalMarks: Int
) {
    fun toModel(): Subject = Subject(
        slNo = slNo,
        code = code,
        name = name,
        banglaName = banglaName,
        teacherName = teacherName,
        theoryHours = theoryHours,
        practicalHours = practicalHours,
        credits = credits,
        theoryCont = theoryCont,
        theoryFinal = theoryFinal,
        practicalCont = practicalCont,
        practicalFinal = practicalFinal,
        totalMarks = totalMarks
    )

    companion object {
        fun fromModel(model: Subject): SubjectEntity = SubjectEntity(
            slNo = model.slNo,
            code = model.code,
            name = model.name,
            banglaName = model.banglaName,
            teacherName = model.teacherName,
            theoryHours = model.theoryHours,
            practicalHours = model.practicalHours,
            credits = model.credits,
            theoryCont = model.theoryCont,
            theoryFinal = model.theoryFinal,
            practicalCont = model.practicalCont,
            practicalFinal = model.practicalFinal,
            totalMarks = model.totalMarks
        )
    }
}

@Entity(tableName = "teachers")
data class TeacherEntity(
    @PrimaryKey val name: String,
    val shortName: String,
    val designation: String,
    val department: String,
    val phone: String,
    val subjectsTaughtJoined: String,
    val isGuideTeacher: Boolean
) {
    fun toModel(): Teacher = Teacher(
        name = name,
        shortName = shortName,
        designation = designation,
        department = department,
        phone = phone,
        subjectsTaught = if (subjectsTaughtJoined.isBlank()) emptyList() else subjectsTaughtJoined.split("||"),
        isGuideTeacher = isGuideTeacher
    )

    companion object {
        fun fromModel(model: Teacher): TeacherEntity = TeacherEntity(
            name = model.name,
            shortName = model.shortName,
            designation = model.designation,
            department = model.department,
            phone = model.phone,
            subjectsTaughtJoined = model.subjectsTaught.joinToString("||"),
            isGuideTeacher = model.isGuideTeacher
        )
    }
}

@Dao
interface ClassSessionDao {
    @Query("SELECT * FROM class_sessions")
    fun getAllClasses(): Flow<List<ClassSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(classes: List<ClassSessionEntity>)

    @Query("DELETE FROM class_sessions")
    suspend fun deleteAll()
}

@Dao
interface SubjectDao {
    @Query("SELECT * FROM subjects ORDER BY slNo ASC")
    fun getAllSubjects(): Flow<List<SubjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(subjects: List<SubjectEntity>)

    @Query("DELETE FROM subjects")
    suspend fun deleteAll()
}

@Dao
interface TeacherDao {
    @Query("SELECT * FROM teachers")
    fun getAllTeachers(): Flow<List<TeacherEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(teachers: List<TeacherEntity>)

    @Query("DELETE FROM teachers")
    suspend fun deleteAll()
}

@Database(
    entities = [ClassSessionEntity::class, SubjectEntity::class, TeacherEntity::class],
    version = 1,
    exportSchema = false
)
abstract class RoutineDatabase : RoomDatabase() {
    abstract fun classSessionDao(): ClassSessionDao
    abstract fun subjectDao(): SubjectDao
    abstract fun teacherDao(): TeacherDao

    companion object {
        @Volatile
        private var INSTANCE: RoutineDatabase? = null

        fun getInstance(context: Context): RoutineDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RoutineDatabase::class.java,
                    "dpi_routine_cache.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
