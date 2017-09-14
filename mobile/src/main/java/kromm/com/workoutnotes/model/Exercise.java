package kromm.com.workoutnotes.model;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Steffen on 17.06.2016.
 */
public class Exercise implements Parcelable
{

    public enum ExerciseState
    {
        DONE, SKIPPED, NORMAL;
    }

    public interface ExerciseChangeListener
    {
        void onChange(Exercise exercise);
    }

    private ExerciseState state = ExerciseState.NORMAL;
    private String name = "Benchpress";
    private Set<ExerciseSet> sets = new HashSet<>();
    private List<ExerciseChangeListener> changeListeners = new ArrayList<>();
    private Drawable icon;

    public Exercise(String name)
    {
        this.name = name;
    }

    public Exercise(Parcel parcel)
    {
        state = ExerciseState.valueOf(parcel.readString());
        name = parcel.readString();
        for (Parcelable p : parcel.readParcelableArray(ExerciseSet.class.getClassLoader())) {
            sets.add((ExerciseSet) p);
        }
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(state.toString());
        dest.writeString(name);
        ExerciseSet[] setsArray = new ExerciseSet[sets.size()];
        sets.toArray(setsArray);
        dest.writeParcelableArray(setsArray, flags);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public Exercise createFromParcel(Parcel in)
        {
            return new Exercise(in);
        }

        public Exercise[] newArray(int size)
        {
            return new Exercise[size];
        }
    };

    public void setState(ExerciseState state)
    {
        this.state = state;
        for (ExerciseChangeListener listener : changeListeners) {
            listener.onChange(this);
        }
    }

    public ExerciseState getState()
    {
        return state;
    }

    public void addSet(ExerciseSet exerciseSet)
    {
        sets.add(exerciseSet);
        for (ExerciseChangeListener listener : changeListeners) {
            listener.onChange(this);
        }
    }

    public String getName()
    {
        return name;
    }

    public Set<ExerciseSet> getSets()
    {
        return sets;
    }

    public void removeSet(ExerciseSet set)
    {
        sets.remove(set);
        for (ExerciseChangeListener listener : changeListeners) {
            listener.onChange(this);
        }
    }

    public void updateExerciseSet(ExerciseSet set)
    {
        sets.add(set);
    }

    public void addChangeListener(ExerciseChangeListener listener)
    {
        changeListeners.add(listener);
    }

    public void removeChangeListener(ExerciseChangeListener listener)
    {
        changeListeners.remove(listener);
    }
}
