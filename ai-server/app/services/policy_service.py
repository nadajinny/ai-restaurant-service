from fastapi import HTTPException, status


class PolicyValidationService:
    def validate_input_text(self, text: str) -> None:
        normalized = text.strip().lower()
        blocked_terms = ["hate", "slur", "ssn", "credit card"]

        if not normalized:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="입력값은 비어 있을 수 없습니다.",
            )

        if any(term in normalized for term in blocked_terms):
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="정책상 허용되지 않는 입력입니다.",
            )

    def validate_output_text(self, text: str) -> None:
        normalized = text.strip().lower()
        blocked_terms = ["hate", "slur", "ssn", "credit card"]

        if any(term in normalized for term in blocked_terms):
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="정책 검증에 실패한 응답입니다.",
            )
