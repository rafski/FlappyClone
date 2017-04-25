package com.rafski.flappyclone;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyClone extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture topTube;
	Texture bottomTube;
	Texture gameOver;
	//ShapeRenderer shapeRenderer;
    Circle birdCircle;
    Rectangle[] topTubeRectanges;
    Rectangle[] bottomTubeRectanges;

	Texture[] birds;
	int flapPosition = 0;
	float birdY = 0;
	float velocity = 0;

	float gravity = 0.9f;

    int gameState = 0;

    float gap = 600;

    Random random;

    float tubeVelocity = 4f;
    int numberOfTubes = 4;
    float[] tubeX = new float[numberOfTubes];
    float[] maxTubeOffset = new float[numberOfTubes];
    float distanceBetweenTubes;

    int score;
    int scoringTube;
    String pointsScore;
    BitmapFont bitmapFont;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
        gameOver = new Texture("gameover.png");
        bitmapFont = new BitmapFont();
        bitmapFont.setColor(Color.CYAN);
        bitmapFont.getData().scale(10);
		score = 0;

        birdCircle = new Circle();

		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");


		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");

        distanceBetweenTubes = Gdx.graphics.getWidth()/2;
        random = new Random();
        topTubeRectanges = new Rectangle[numberOfTubes];
        bottomTubeRectanges = new Rectangle[numberOfTubes];

        startSetup();
	}

	public void startSetup(){
        birdY = Gdx.graphics.getHeight()/2- birds[flapPosition].getHeight()/2;

        for (int i = 0; i< numberOfTubes ;i++){

            maxTubeOffset[i] = (random.nextFloat() -0.5f) * (Gdx.graphics.getHeight() - gap - 100);
            tubeX[i] =Gdx.graphics.getWidth()/2 - topTube.getWidth()/2 + Gdx.graphics.getWidth() + i * distanceBetweenTubes;
            topTubeRectanges[i] = new Rectangle();
            bottomTubeRectanges[i] = new Rectangle();
        }
    }

	@Override
	public void render () {

        batch.begin();
        batch.draw(background, 0,0, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());


        if (gameState == 1) {

            if (tubeX[scoringTube] < Gdx.graphics.getWidth()/2){

                score++;

                Gdx.app.log("score", String.valueOf(score));

                if (scoringTube < numberOfTubes - 1){

                    scoringTube++;
                }else {
                    scoringTube = 0;
                }

            }

            if (Gdx.input.justTouched()){

                velocity = -10;
                gameState = 1;


            }

            for (int i = 0; i< numberOfTubes ;i++) {

                if (tubeX[i] < - topTube.getWidth()){

                    tubeX[i] += numberOfTubes * distanceBetweenTubes;
                    maxTubeOffset[i] = (random.nextFloat() -0.5f) * (Gdx.graphics.getHeight() - gap - 100);
                }else{
                    tubeX[i] = tubeX[i] - tubeVelocity;


                }

                topTubeRectanges[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() - topTube.getHeight() + gap + maxTubeOffset[i],
                        topTube.getWidth(),topTube.getHeight());
                bottomTubeRectanges[i] = new Rectangle(tubeX[i], 0 - gap + maxTubeOffset[i],
                        bottomTube.getWidth(),bottomTube.getHeight());



                batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() - topTube.getHeight() + gap + maxTubeOffset[i]);
                batch.draw(bottomTube, tubeX[i], 0 - gap + maxTubeOffset[i]);
            }

            if (birdY > 0){

                velocity = velocity + gravity;
                birdY -= velocity;
            }else{

                gameState = 2;

            }


        }else if (gameState == 0){
            if (Gdx.input.justTouched()){

                gameState = 1;
            }
        }else if(gameState == 2){
            batch.draw(gameOver, Gdx.graphics.getWidth()/2 - gameOver.getWidth()/2, Gdx.graphics.getHeight()/2 - gameOver.getHeight()/2);
            velocity = 300;
            if (Gdx.input.justTouched()){

                gameState = 1;
                startSetup();
                score = 0;
                velocity = 0;
                scoringTube = 0;
            }

        }

        if (flapPosition == 0){
            flapPosition =1;
        }else{
            flapPosition = 0;
        }


        batch.draw(birds[flapPosition], Gdx.graphics.getWidth()/2 - birds[flapPosition].getWidth()/2, birdY);

        bitmapFont.draw(batch, String.valueOf(score), 100, 200);
        batch.end();

        birdCircle.set(Gdx.graphics.getWidth()/2, birdY + birds[flapPosition].getHeight()/2, birds[flapPosition].getWidth()/2);

        //shapeRenderer = new ShapeRenderer();
        //shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        //shapeRenderer.setColor(Color.CHARTREUSE);
        //shapeRenderer.circle(birdCircle.x,birdCircle.y, birdCircle.radius);

        for (int i =0; i <numberOfTubes; i++){
            //shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() - topTube.getHeight() + gap + maxTubeOffset[i], topTube.getWidth(),topTube.getHeight());
            //shapeRenderer.rect(tubeX[i], 0 - gap + maxTubeOffset[i], bottomTube.getWidth(),bottomTube.getHeight());

            if(Intersector.overlaps(birdCircle, topTubeRectanges[i]) || Intersector.overlaps(birdCircle,bottomTubeRectanges[i])){

               Gdx.app.log("Crash","bang!");
                gameState = 2;

            }
        }


        //shapeRenderer.end();

	}
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
	}
}
