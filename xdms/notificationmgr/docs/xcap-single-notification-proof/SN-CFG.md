# SN-CFG — Runtime configuration loaded

| Field | Value |
|---|---|
| Jira | 2.2 System Configurations |
| Type | Positive |
| Result | Pass / Fail |

## Requirement
`XCAP_NOTIFICATION_OPTIMIZED`, `XCAP_NOTIFICATION_PERIOD` (default 120), `XCAP_DIFF_PAYLOAD_SIZE` (default 2048) must be loaded by the running XDM process.

## Steps
1. Set C&P values (optimized 1 or 0 per later cases).
2. Restart XDM if poll interval is only read at schedule time.
3. Capture startup / first-poll logs.

## Expected
- ON: `STEP-SCH2` `effectiveIntervalSeconds=1`, `STEP-HG1 optimizedMode=true`, `STEP-CONF1 rawFlag=1 enabled=true`
- Payload: `STEP-9A effectiveValue=` matches config (on first bundle send)

## Proof commands
```bash
grep -nE "STEP-SCH2|STEP-HG1 Poll start|STEP-CONF1|STEP-9A Payload threshold" XDMDataMgr.log | tail -20
```

## Evidence captured
**Build/JAR:**  
**Config values:** OPTIMIZED=___ PERIOD=___ PAYLOAD=___  
**Log snippet:**

```
(paste)
```

**Pass/Fail + notes:**
