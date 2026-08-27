/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.filewatcher;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import com.kodiak.logger.KnLogger;



public class KnFileWatcherServiceImpl implements KnFileWatcherService, Runnable {

	private final WatchService											mWatchService;
	private final AtomicBoolean											serviceIsUp;
	private final ConcurrentMap<WatchKey, Path>							mWatchKeyToDirPathMap;
	private final ConcurrentMap<Path, Set<OnFileChangeListener>>		mDirPathToListenersMap;
	private final ConcurrentMap<OnFileChangeListener, Set<PathMatcher>>	mListenerToFilePatternsMap;
	private static final KnLogger										knLogger	= KnLogger.getLogger(KnFileWatcherServiceImpl.class);
	private static long lastUpdatedTime = 0;
	private Set<String> watchedFiles;

	public KnFileWatcherServiceImpl() throws IOException {
		mWatchService = FileSystems.getDefault().newWatchService();
		serviceIsUp = new AtomicBoolean(false);
		mWatchKeyToDirPathMap = newConcurrentMap();
		mDirPathToListenersMap = newConcurrentMap();
		mListenerToFilePatternsMap = newConcurrentMap();
		watchedFiles = newConcurrentSet();
	}

	@SuppressWarnings("unchecked")
	private static <T> WatchEvent<T> cast(WatchEvent<?> event)
	{
		return (WatchEvent<T>) event;
	}

	private static <K, V> ConcurrentMap<K, V> newConcurrentMap()
	{
		return new ConcurrentHashMap<>();
	}

	private static <T> Set<T> newConcurrentSet()
	{
		return Collections.newSetFromMap(newConcurrentMap());
	}

	public static PathMatcher matcherForGlobExpression(String globPattern)
	{
		return FileSystems.getDefault().getPathMatcher("glob:" + globPattern);
	}

	public static boolean matches(Path input, PathMatcher pattern)
	{
		return pattern.matches(input);
	}

	public static boolean matchesAny(Path input, Set<PathMatcher> patterns)
	{
		for (PathMatcher pattern : patterns) {
			if (matches(input, pattern)) {
				return true;
			}
		}

		return false;
	}

	private Path getDirPath(WatchKey key)
	{
		return mWatchKeyToDirPathMap.get(key);
	}

	private Set<OnFileChangeListener> getListeners(Path dir)
	{
		return mDirPathToListenersMap.get(dir);
	}

	private Set<PathMatcher> getPatterns(OnFileChangeListener listener)
	{
		return mListenerToFilePatternsMap.get(listener);
	}

	private Set<OnFileChangeListener> matchedListeners(Path dir, Path file)
	{
		String method = "matchedListeners()";
//		knLogger.debug(method, "Modified is getting called");
		return getListeners(dir).stream().filter(listener -> matchesAny(file, getPatterns(listener))).collect(Collectors.toSet());
	}

	private void notifyListeners(WatchKey key)
	{
		String method = "notifyListeners()";
		knLogger.debug(method,"Watched files ", watchedFiles);
		for (WatchEvent<?> event : key.pollEvents()) {

			WatchEvent.Kind eventKind = event.kind();
			synchronized (eventKind) {
				if (eventKind.equals(StandardWatchEventKinds.OVERFLOW)) {
					knLogger.info(method, "eventKind: " + eventKind);
					return;
				}

				WatchEvent<Path> pathEvent = cast(event);
				Path file = pathEvent.context();
				String filename = file.getFileName().toString();
				knLogger.debug(method,"filename ", filename);
				long currentTime = System.currentTimeMillis();
//				if ((currentTime - lastUpdatedTime) < 1000 || !watchedFiles.contains(filename)) {
				if (!watchedFiles.contains(filename)) {
					knLogger.debug(method, "Ignoring notification for ", filename, "of type ", eventKind, " at ",
							currentTime);
					continue;
				}
//				lastUpdatedTime = currentTime;
				knLogger.debug(method, "Processing notification for ", filename, "of type ", eventKind, " at ",
						currentTime);
				OnFileChangeListener listener = getListeners(getDirPath(key)).iterator().next();
				if (eventKind.equals(StandardWatchEventKinds.ENTRY_CREATE)) {
					listener.onFileCreate(getDirPath(key).toFile().toString(), file.toString());
				} else if (eventKind.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
					listener.onFileModify(getDirPath(key).toFile().toString(), file.toString());
				}
			}
		}
	}

	@Override
	public void register(OnFileChangeListener listener, String dirPath, String[] globPatterns) throws IOException
	{
		String method = "register";
		Path dir = Paths.get(dirPath);
		knLogger.info(method,"Registering watcher for ", dir.toString(), globPatterns);

		if (!Files.isDirectory(dir)) {
			knLogger.info(method, dirPath + " is not a directory.");
			throw new IllegalArgumentException(dirPath + " is not a directory.");
		}
		try	{
			watchedFiles.addAll(Arrays.asList(globPatterns));
			if (!mDirPathToListenersMap.containsKey(dir)) {
				WatchKey key = dir.register(mWatchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);
				knLogger.info(method,"Created watch key ", key);
				mWatchKeyToDirPathMap.put(key, dir);
				mDirPathToListenersMap.put(dir, newConcurrentSet());
			}
	
			getListeners(dir).add(listener);
	
			Set<PathMatcher> patterns = newConcurrentSet();
	
			for (String globPattern : globPatterns) {
				patterns.add(matcherForGlobExpression(globPattern));
			}
	
			if (patterns.isEmpty()) {
				patterns.add(matcherForGlobExpression("*")); // Match everything if no filter is found
			}
	
			mListenerToFilePatternsMap.put(listener, patterns);
	
			knLogger.info(method, "Watching files matching " + Arrays.toString(globPatterns) + " under " + dirPath + " for changes.");
		}
		catch(Exception exe)	{
			knLogger.debug(method, "Exception while initializing file watcher ", exe);
		}
	}

	@Override
	public void start()
	{
		if (serviceIsUp.compareAndSet(false, true)) {
			Thread runnerThread = new Thread(this, KnFileWatcherService.class.getSimpleName());
			runnerThread.start();
		}
	}

	@Override
	public void stop()
	{
		// Kill thread lazily
		serviceIsUp.set(false);
	}

	@Override
	public void run()
	{
		String method = "run";
		knLogger.info(method, "Starting file watcher service.");

		while (serviceIsUp.get()) {
			WatchKey key;
			try {
				key = mWatchService.take();
			} catch (InterruptedException e) {
				knLogger.info(KnFileWatcherService.class.getSimpleName() + " service interrupted.");
				break;
			}

			if (null == getDirPath(key)) {
				knLogger.error("Watch key not recognized.");
				continue;
			}

			notifyListeners(key);
			boolean valid = key.reset();
			if (!valid) {
				mWatchKeyToDirPathMap.remove(key);
				if (mWatchKeyToDirPathMap.isEmpty()) {
					break;
				}
			}
		}

		serviceIsUp.set(false);
		knLogger.info(method, "Stopping file watcher service.");
	}
}