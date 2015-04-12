package fr.mydedibox.libarcade.emulator.sdl;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.greatlittleapps.utility.Utility;

import fr.mydedibox.libarcade.emulator.activity.Main;

public class SDLSurface extends SurfaceView implements SurfaceHolder.Callback/*, OnKeyListener*/
{
	private final Context mCtx;
	
    public Thread mSDLThread;    
    
    //private EGLContext  mEGLContext;
    private EGLSurface  mEGLSurface;
    private EGLDisplay  mEGLDisplay;
    
    //private HardwareInput mHardwareInput;
    //public boolean useHardwareButtons = false;

    public SDLSurface(Context context, AttributeSet attrs, int defStyle) 
    {
        super(context, attrs, defStyle);
        this.mCtx = context;
        init();
    }
    
    // Startup    
    public SDLSurface( Context context, AttributeSet attrs ) 
    {
        super(context, attrs);
        this.mCtx = context;
        init(); 
    }
    
    void init()
    {
    	setWillNotDraw(false);
    	
    	//getHolder().setType( surfaceViewHolder.surfaceView_TYPE_GPU );
        //Utility.log( "Holder size: " + mScreenHolderSizeX +"x"+ mScreenHolderSizeY );
    	getHolder().setFixedSize( Main.mScreenHolderSizeX, Main.mScreenHolderSizeY );
    	getHolder().addCallback(this); 
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();    
    }
    
    // Called when we have a valid drawing surface
    public void surfaceCreated(SurfaceHolder holder) {
        //Utility.log("surfaceCreated()");
        //enableSensor(Sensor.TYPE_ACCELEROMETER, true);
    }
    
    // Called when we lose the surface
    public void surfaceDestroyed(SurfaceHolder holder) 
    {
    	Utility.log("surfaceDestroyed()");
    	//Main.stop();
    }
 
    // Called when the surface is resized
    @SuppressWarnings("deprecation")
	public void surfaceChanged(SurfaceHolder holder,
                               int format, int width, int height) {
    	
    	Utility.log("surfaceChanged(): " + width + ":" + height);

        int sdlFormat = 0x85151002; // SDL_PIXELFORMAT_RGB565 by default
        switch (format) {
        case PixelFormat.A_8:
            Utility.log("pixel format A_8");
            break;
        case PixelFormat.LA_88:
            Utility.log("pixel format LA_88");
            break;
        case PixelFormat.L_8:
            Utility.log("pixel format L_8");
            break;
        case PixelFormat.RGBA_4444:
            Utility.log("pixel format RGBA_4444");
            sdlFormat = 0x85421002; // SDL_PIXELFORMAT_RGBA4444
            break;
        case PixelFormat.RGBA_5551:
            Utility.log("pixel format RGBA_5551");
            sdlFormat = 0x85441002; // SDL_PIXELFORMAT_RGBA5551
            break;
        case PixelFormat.RGBA_8888:
            Utility.log("pixel format RGBA_8888");
            sdlFormat = 0x86462004; // SDL_PIXELFORMAT_RGBA8888
            break;
        case PixelFormat.RGBX_8888:
            Utility.log("pixel format RGBX_8888");
            sdlFormat = 0x86262004; // SDL_PIXELFORMAT_RGBX8888
            break;
        case PixelFormat.RGB_332:
            Utility.log("pixel format RGB_332");
            sdlFormat = 0x84110801; // SDL_PIXELFORMAT_RGB332
            break;
        case PixelFormat.RGB_565:
            Utility.log("pixel format RGB_565");
            sdlFormat = 0x85151002; // SDL_PIXELFORMAT_RGB565
            break;
        case PixelFormat.RGB_888:
            Utility.log("pixel format RGB_888");
            // Not sure this is right, maybe SDL_PIXELFORMAT_RGB24 instead?
            sdlFormat = 0x86161804; // SDL_PIXELFORMAT_RGB888
            break;
        default:
            Utility.log("pixel format unknown " + format);
            break;
        }
        
        SDLJni.onNativeResize(width, height, sdlFormat);

        // Now start up the C app thread
        if (mSDLThread == null) {
            mSDLThread = new Thread( new SDLMain( mCtx), "SDLThread" ); 
            mSDLThread.start();       
        }
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) 
    {
    	Utility.log( "onMeasure" );
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
    	Utility.log( "onSizeChanged" );
        super.onSizeChanged(w, h, oldw, oldh);
    }

   	@Override
    public void onDraw(Canvas canvas) 
    {
    	Utility.log( "onDraw" );
    	super.onDraw(canvas);
    }
    
    // EGL functions
    public boolean initEGL(int majorVersion, int minorVersion) 
    {
        //Utility.log("Starting up OpenGL ES " + majorVersion + "." + minorVersion);
        try 
        {
            EGL10 egl = (EGL10)EGLContext.getEGL();

            EGLDisplay dpy = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

            int[] version = new int[2];
            egl.eglInitialize(dpy, version);

            int EGL_OPENGL_ES_BIT = 1;
            int EGL_OPENGL_ES2_BIT = 4;
            int renderableType = 0;
            if (majorVersion == 2) {
                renderableType = EGL_OPENGL_ES2_BIT;
            } else if (majorVersion == 1) {
                renderableType = EGL_OPENGL_ES_BIT;
            }
            int[] configSpec = {
                //EGL10.EGL_DEPTH_SIZE,   16,
                EGL10.EGL_RENDERABLE_TYPE, renderableType,
                EGL10.EGL_NONE
            };
            EGLConfig[] configs = new EGLConfig[1];
            int[] num_config = new int[1];
            if (!egl.eglChooseConfig(dpy, configSpec, configs, 1, num_config) || num_config[0] == 0) {
                Utility.log("No EGL config available");
                return false;
            }
            EGLConfig config = configs[0];

            int EGL_CONTEXT_CLIENT_VERSION=0x3098;
            int contextAttrs[] = new int[]
            {
                EGL_CONTEXT_CLIENT_VERSION, majorVersion,
                EGL10.EGL_NONE
            }; 
            EGLContext ctx = egl.eglCreateContext(dpy, config, EGL10.EGL_NO_CONTEXT, contextAttrs);
            if (ctx == EGL10.EGL_NO_CONTEXT) {
                Utility.log("Couldn't create context");
                return false;
            }

            EGLSurface surface = egl.eglCreateWindowSurface(dpy, config, this, null);
            if (surface == EGL10.EGL_NO_SURFACE) {
                Utility.log("Couldn't create surface");
                return false;
            }

            if (!egl.eglMakeCurrent(dpy, surface, surface, ctx)) {
                Utility.log("Couldn't make context current");
                return false;
            }

            //mEGLContext = ctx;
            mEGLDisplay = dpy;
            mEGLSurface = surface;
        } 
        catch(Exception e) 
        {
            Utility.log(e + "");
            for (StackTraceElement s : e.getStackTrace()) 
            {
                Utility.log(s.toString());
            }
        }
        return true;
    }
   
    // EGL buffer flip
    public void flipEGL() 
    {
        try 
        {
            EGL10 egl = (EGL10)EGLContext.getEGL();
            egl.eglWaitNative(EGL10.EGL_CORE_NATIVE_ENGINE, null);
            // drawing here
            egl.eglWaitGL();
            egl.eglSwapBuffers(mEGLDisplay, mEGLSurface);
        } 
        catch(Exception e) 
        {
            Utility.log("flipEGL(): " + e);
            for (StackTraceElement s : e.getStackTrace()) 
            {
                Utility.log(s.toString());
            }
        }
    }
    
    // Java functions called from C
    public static boolean createGLContext(int majorVersion, int minorVersion) 
    {
        return Main.surfaceView.initEGL(majorVersion, minorVersion);
    }
    public static void flipBuffers() 
    {
    	Main.surfaceView.flipEGL();
    }
}
