package com.example.jgwhit14.cs450fall18_hw2_montyhall;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class GameFragment extends Fragment {

    // Define variables for our views
    private ImageButton door1 = null;
    private ImageButton door2 = null;
    private ImageButton door3 = null;
    private boolean imgClicked = false;
    private int carDoor = -1;
    private LinearLayout keepChange = null;
    private LinearLayout playAgain = null;
    private Button keepBtn = null;
    private Button changeBtn = null;
    private  Button playAgainBtn = null;
    public int wins;
    public int losses;
    public int total;
    private TextView winView = null;
    private TextView lossView = null;
    private TextView totalView = null;
    Timer t;
    Counter ctr;

    public AudioAttributes aa = null;
    private SoundPool soundPool = null;
    private int carSound = 0;
    private int goatSound = 1;
    private int clickSound = 2;


    private int doorChosen = -1;
    private int doorToOpen = -1;

    public GameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        gameSetup();

        this.aa = new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).setUsage(AudioAttributes.USAGE_GAME).build();

        this.soundPool = new SoundPool.Builder().setMaxStreams(1).setAudioAttributes(aa).build();
        this.carSound = this.soundPool.load(this.getContext(), R.raw.car, 1);
        this.goatSound = this.soundPool.load(this.getContext(), R.raw.goat, 1);
        this.clickSound = this.soundPool.load(this.getContext(), R.raw.click, 1);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View currView = inflater.inflate(R.layout.fragment_game, container, false);
        initializeViews(currView);
        return currView;
    }

    @Override
    public void onPause() {
        super.onPause();

        this.getActivity().getPreferences(MODE_PRIVATE).edit().putInt("WINS", wins).apply();
        this.getActivity().getPreferences(MODE_PRIVATE).edit().putInt("LOSSES", losses).apply();
        this.getActivity().getPreferences(MODE_PRIVATE).edit().putInt("TOTAL", total).apply();
    }

    private void initializeViews(View currView) {
        // initialize views
        this.door1 = currView.findViewById(R.id.door1);
        this.door2 = currView.findViewById(R.id.door2);
        this.door3 = currView.findViewById(R.id.door3);
        this.keepChange = currView.findViewById(R.id.keepChange);
        this.keepBtn = currView.findViewById(R.id.keepBtn);
        this.keepBtn.setSoundEffectsEnabled(false);
        this.changeBtn = currView.findViewById(R.id.changeBtn);
        this.changeBtn.setSoundEffectsEnabled(false);
        this.playAgain = currView.findViewById(R.id.playAgain);
        this.playAgainBtn = currView.findViewById(R.id.playAgainBtn);
        this.playAgainBtn.setSoundEffectsEnabled(false);
        this.winView = currView.findViewById(R.id.winsText);
        this.lossView = currView.findViewById(R.id.lossText);
        this.totalView = currView.findViewById(R.id.totalText);


        boolean contClicked = this.getActivity().getSharedPreferences("MontyHall", MODE_PRIVATE).getBoolean("CONT_CLICKED", false);
        boolean newClicked = this.getActivity().getSharedPreferences("MontyHall", MODE_PRIVATE).getBoolean("NEW_CLICKED", false);

        if (contClicked) {
            this.wins = this.getActivity().getPreferences(MODE_PRIVATE).getInt("WINS", 0);
            this.losses = this.getActivity().getPreferences(MODE_PRIVATE).getInt("LOSSES", 0);
            this.total = this.getActivity().getPreferences(MODE_PRIVATE).getInt("TOTAL", 0);
        } else if (newClicked) {
            this.wins = 0;
            this.losses = 0;
            this.total = 0;
        } else {
            // Hopefully will never get here...
            this.wins = 0;
            this.losses = 0;
            this.total = 0;
        }


        winView.setText("\t"+Integer.toString(wins));
        lossView.setText("\t"+Integer.toString(losses));
        totalView.setText("\t"+Integer.toString(total));


        this.door1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animator anim = AnimatorInflater.loadAnimator(getContext(), R.animator.doorshake);
                anim.setTarget(door1);
                anim.start();
                if (!imgClicked){
                    doorChosen = 1;
                    door1.setImageLevel(1);
                    imgClicked = true;
                    showGoat();
                    keepChange.setVisibility(View.VISIBLE);
                }
            }
        });
        this.door2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animator anim = AnimatorInflater.loadAnimator(getContext(), R.animator.doorshake);
                anim.setTarget(door2);
                anim.start();
                if (!imgClicked){
                    doorChosen = 2;
                    door2.setImageLevel(1);
                    imgClicked = true;
                    showGoat();
                    keepChange.setVisibility(View.VISIBLE);
                }
            }
        });
        this.door3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animator anim = AnimatorInflater.loadAnimator(getContext(), R.animator.doorshake);
                anim.setTarget(door3);
                anim.start();
                if (!imgClicked){
                    doorChosen = 3;
                    door3.setImageLevel(1);
                    imgClicked = true;
                    showGoat();
                    keepChange.setVisibility(View.VISIBLE);
                }
            }
        });

        this.keepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(clickSound, 1f,
                        1f, 1, 0, 1f);
                t = new Timer();
                ctr = new Counter();
                t.scheduleAtFixedRate(ctr, 0, 1000);
                keepChange.setVisibility(View.INVISIBLE);

            }
        });

        this.changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(clickSound, 1f,
                        1f, 1, 0, 1f);
                switch (doorChosen){
                    case 1:
                        door1.setImageLevel(0);
                        break;
                    case 2:
                        door2.setImageLevel(0);
                        break;
                    case 3:
                        door3.setImageLevel(0);
                        break;
                    default:
                        System.out.println("You can't be here!");
                }

                if(doorChosen != 1 && doorToOpen != 1){
                    doorChosen = 1;
                } else if (doorChosen != 2 && doorToOpen != 2){
                    doorChosen = 2;
                } else if (doorChosen != 3 && doorToOpen != 3){
                    doorChosen = 3;
                }
                t = new Timer();
                ctr = new Counter();
                t.scheduleAtFixedRate(ctr, 0, 1000);
                keepChange.setVisibility(View.INVISIBLE);

            }
        });

        this.playAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(clickSound, 1f,
                        1f, 1, 0, 1f);
                door1.setImageLevel(0);
                door2.setImageLevel(0);
                door3.setImageLevel(0);
                imgClicked = false;
                gameSetup();
                playAgain.setVisibility(View.INVISIBLE);
            }
        });

    }


    private void checkWin() {
        if (carDoor == doorChosen){
            wins++;
            winView.setText("\t"+Integer.toString(wins));
            soundPool.play(carSound, 1f,
                    1f, 1, 0, 1f);
        } else {
            losses++;
            lossView.setText("\t"+Integer.toString(losses));
            soundPool.play(goatSound, 1f,
                    1f, 1, 0, 1f);
        }
        total++;
        totalView.setText("\t"+Integer.toString(total));
        playAgain.setVisibility(View.VISIBLE);
    }



    class Counter extends TimerTask {
        private int count;

        @Override
        public void run() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ImageButton dto = getDoorChoosen();
                    count++;
                    if (count == 1){
                        dto.setImageLevel(4);
                    } else if (count == 2){
                        dto.setImageLevel(5);
                    } else if (count == 3){
                        dto.setImageLevel(6);
                    } else if (count == 4) {
                        if (doorChosen == carDoor) {
                            dto.setImageLevel(2);
                        } else {
                            dto.setImageLevel(3);
                        }
                        ctr.cancel();
                        t.cancel();
                        checkWin();
                    }
                }
            });

        }
    }

    private ImageButton getDoorChoosen(){
        switch (doorChosen) {
            case 1:
                return door1;
            case 2:
                return door2;
            case 3:
                return door3;
            default:
                System.out.println("GO AWAY, NO ONE IS HOME!");
                return null;
        }
    }



    public void gameSetup(){
        Random randomizer = new Random();
        carDoor = randomizer.nextInt(3) + 1;
    }

    public void showGoat(){
        System.out.println("DOOR CHOSEN: " + doorChosen + " CAR BEHIND: " + carDoor);
        ArrayList<Integer> choices = new ArrayList<Integer>();
        if(carDoor != 1 && doorChosen != 1){
            choices.add(1);
        }
        if(carDoor != 2 && doorChosen != 2){
            choices.add(2);
        }
        if(carDoor != 3 && doorChosen != 3){
            choices.add(3);
        }

        Random randomizer = new Random();
        doorToOpen = choices.get(randomizer.nextInt(choices.size()));

        System.out.println("DOOR TO OPEN: " + doorToOpen);

        switch (doorToOpen){
            case 1:
                door1.setImageLevel(3);
                break;
            case 2:
                door2.setImageLevel(3);
                break;
            case 3:
                door3.setImageLevel(3);
                break;
            default:
                System.out.println("How did you possibly get here?");
        }
        soundPool.play(goatSound, 1f,
                1f, 1, 0, 1f);

    }




}

